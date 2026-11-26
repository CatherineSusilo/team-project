package usecase.transfercontents;

import entity.Action;
import entity.ActionKind;
import entity.Bench;
import entity.BuchnerFunnel;
import entity.ChemistryEngine;
import entity.FiltrationFlask;
import entity.ItemType;
import entity.Lab;
import entity.LabItem;
import entity.Mixture;
import entity.PasteurPipette;
import entity.StockBottle;
import entity.Substance;
import entity.Substances;
import entity.UnknownSample;
import entity.Vessel;
import java.io.UncheckedIOException;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Carries out a transfer: checks the combination is one from the lab manual, then lets the
 * chemistry engine add it (or mixes the container), and shows the result. Anything not in the
 * lab manual (e.g. HCl into the ether cylinder) is rejected and nothing changes.
 */
public class TransferContentsInteractor implements TransferContentsInputBoundary {
  private final LabDataAccessInterface dataAccess;
  private final TransferContentsOutputBoundary presenter;
  private final ChemistryEngine chemistry;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab (and its bench) is kept
   * @param presenter receives the result
   * @param chemistry applies the chemistry
   */
  public TransferContentsInteractor(LabDataAccessInterface dataAccess,
      TransferContentsOutputBoundary presenter, ChemistryEngine chemistry) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
    this.chemistry = chemistry;
  }

  @Override
  public void execute(TransferContentsInputData in) {
    Lab lab = dataAccess.getLab();
    Bench bench = lab.getBench();
    // get the mixture in the target container, check if it exists and the amount is valid
    Mixture target = mixtureOf(bench, in.getTargetId());
    // target is null means it was not dropped into a container, return error
    if (target == null) {
      presenter.prepareFailView("Drop it onto a container.");
      return;
    }
    // invalid amount input (0 or negative) return error
    if (in.getAmount() <= 0) {
      presenter.prepareFailView("Choose an amount above 0.");
      return;
    }

    // if the student is mixing, execute mixing and return success
    if (in.getUnit() == TransferUnit.MIXES) {
      // pumping only works with liquid in the tube (powder alone can't be mixed)
      if (target.totalVolume() == 0) {
        presenter.prepareFailView("There's nothing to mix yet.");
        return;
      }
      dataAccess.pushSnapshot();                  // before changing anything, so Undo can go back
      target.mix((int) Math.round(in.getAmount()));
      // the pipette becomes this tube's mixing pipette, so it can keep pumping (A5, then A7)
      PasteurPipette pumping = pipetteOf(bench, in.getSourceId());
      if (pumping != null) {
        pumping.markMixing(in.getTargetId());
      }
      finish(lab, new Action(ActionKind.MIX, in.getSourceId(), in.getTargetId(),
          in.getAmount(), null, ""));
      return;
    }

    // the unknown sample: a spatula scoop of grams goes into the conical tube (A4)
    // (the vial on the bench stands for the student's unknown sample)
    if (typeOf(bench, in.getSourceId()) == ItemType.VIAL) {
      if (typeOf(bench, in.getTargetId()) != ItemType.CONICAL_TUBE) {
        presenter.prepareFailView("The unknown goes into the conical tube.");
        return;
      }
      UnknownSample unknown = lab.getUnknown();
      dataAccess.pushSnapshot();
      unknown.addTo(target, in.getAmount());
      finish(lab, new Action(ActionKind.MOVE_LIQUID, in.getSourceId(), in.getTargetId(),
          in.getAmount(), null, ""));
      return;
    }

    // everything else is a liquid: drops become mL (10 drops = 0.5 mL)
    double ml;
    if (in.getUnit() == TransferUnit.DROPS) {
      ml = in.getAmount() * StockBottle.DROP_ML;
    } else {
      ml = in.getAmount();
    }

    // B3: tip the test tube onto the funnel; the liquid runs through into the flask under it
    if (typeOf(bench, in.getTargetId()) == ItemType.BUCHNER_FUNNEL) {
      filter(lab, bench, in, ml);
      return;
    }

    // it has to fit, e.g. a pipette holds 2 mL
    // (the tiny 1e-9 allows for rounding: 40 drops of 0.05 mL add up to 2.000000000000001)
    if (target.totalVolume() + ml > capacityOf(bench, in.getTargetId()) + 1e-9) {
      presenter.prepareFailView("That won't fit.");
      return;
    }

    // otherwise, the student is transferring a substance into a container, check if it is allowed
    Substance added = substanceOf(bench, in.getSourceId());
    if (added != null) {
      // a stock bottle (NaOH, HCl, water, ether)
      // check if the substance is allowed (by the lab manual) to be added to the target container
      ItemType targetType = typeOf(bench, in.getTargetId());
      if (!isAllowed(added, targetType)) {
        presenter.prepareFailView(whereItGoes(added));
        return;
      }

      // after checking everything is valid, add the substance to the mixture and return success
      dataAccess.pushSnapshot();
      chemistry.addReagent(target, added, ml);
      markPipettesUsed(bench, in);                // e.g. a pipette drawing ether (A5)
      finish(lab, new Action(ActionKind.ADD, in.getSourceId(), in.getTargetId(), ml, null, ""));
      return;
    }

    // a container (cylinder, tube, pipette): the pipette draws from the bottom and empties
    Mixture source = mixtureOf(bench, in.getSourceId());
    // only if it actually holds liquid (a tube with just powder has nothing to pour)
    if (source != null && source.totalVolume() > 0) {
      if (!usesPipette(bench, in.getSourceId(), in.getTargetId())) {
        presenter.prepareFailView("Use a pipette to move liquid.");
        return;
      }
      dataAccess.pushSnapshot();
      target.pour(source.drawFromBottom(ml));
      markPipettesUsed(bench, in);
      finish(lab, new Action(ActionKind.MOVE_LIQUID, in.getSourceId(), in.getTargetId(), ml,
          null, ""));
      return;
    }

    // student tried adding something that is empty, return error
    presenter.prepareFailView("There's nothing in that to pour.");
  }
  
  // Helper functions

  // Whether this is a valid transfer, referring to the lab manual for valid transfers.
  private boolean isAllowed(Substance added, ItemType target) {
    if (added == Substances.NAOH) {
      // Task A: NaOH is only allowed in the conical tube
      return target == ItemType.CONICAL_TUBE;          
    } else if (added == Substances.HCL) {
      // Task B: HCl is only allowed in the test tube
      return target == ItemType.TEST_TUBE;            
    } else if (added == Substances.WATER || added == Substances.ETHER) {
      // water and ether are allowed in the conical tube or its graduated cylinder
      // (and in a pipette, which draws it up from the stock bottle)
      return target == ItemType.CONICAL_TUBE || target == ItemType.GRADUATED_CYLINDER
          || target == ItemType.PIPETTE;
    } else if (added == Substances.ASPIRIN || added == Substances.MEFENAMIC_ACID
        || added == Substances.VANILLIN) {
      // unknowns are only allowed in the conical tube
      return target == ItemType.CONICAL_TUBE;         
    }
    return false;                                       
  }

  // Liquid only moves between containers with a pipette: one end has to be the pipette.
  private boolean usesPipette(Bench bench, String sourceId, String targetId) {
    return typeOf(bench, sourceId) == ItemType.PIPETTE
        || typeOf(bench, targetId) == ItemType.PIPETTE;
  }

  // A pipette that has touched liquid is used, so the student takes a new one where the lab
  // manual says to (A5: new pipette for mixing, B4: new dry one for the ether rinse).
  private void markPipettesUsed(Bench bench, TransferContentsInputData in) {
    PasteurPipette source = pipetteOf(bench, in.getSourceId());
    if (source != null) {
      source.markUsed();
    }
    PasteurPipette target = pipetteOf(bench, in.getTargetId());
    if (target != null) {
      target.markUsed();
    }
  }

  // Extra: Gives a hint to the student after doing something wrong every time.
  // The hint shown when the student puts a substance in the wrong place.
  private String whereItGoes(Substance added) {
    if (added == Substances.NAOH) {
      return "NaOH goes into the conical tube.";
    } else if (added == Substances.HCL) {
      return "HCl goes into the aqueous test tube.";
    } else if (added == Substances.WATER || added == Substances.ETHER) {
      return added.getName() + " goes into the conical tube or its graduated cylinder.";
    } else if (added == Substances.ASPIRIN || added == Substances.MEFENAMIC_ACID
        || added == Substances.VANILLIN) {
      return "The unknown goes into the conical tube.";
    }
    return "That isn't used in this lab.";
  }

  // B3: pouring onto the Büchner funnel. The liquid runs through into the flask under it (the
  // filtrate) and the solid stays on the paper (no paper: it goes through too). Pouring the
  // test tube on again once there's solid on the funnel is a rinse.
  private void filter(Lab lab, Bench bench, TransferContentsInputData in, double ml) {
    BuchnerFunnel funnel = (BuchnerFunnel) bench.getItem(in.getTargetId());
    FiltrationFlask flask = flaskUnder(bench, funnel.getId());
    if (flask == null || !flask.isAssembled()) {
      presenter.prepareFailView("Set up the filtration first.");
      return;
    }
    Mixture source = mixtureOf(bench, in.getSourceId());
    if (source == null || source.getLayers().isEmpty()) {
      presenter.prepareFailView("There's nothing in that to pour.");
      return;
    }
    // the tube poured on again after the solid is on the paper: that's a rinse
    final boolean rinse = typeOf(bench, in.getSourceId()) == ItemType.TEST_TUBE
        && drug(funnel.getMixture()) > 0;
    dataAccess.pushSnapshot();
    flask.getMixture().pour(source.drawFromBottom(ml));   // the liquid goes through
    if (source.totalVolume() == 0) {
      // tipped out: the solid left in it comes along too
      if (funnel.hasPaper()) {
        funnel.getMixture().pour(source);             // stays on the paper
      } else {
        flask.getMixture().pour(source);              // no paper: straight through
      }
    }
    if (rinse) {
      funnel.addRinse();
    }
    finish(lab, new Action(ActionKind.MOVE_LIQUID, in.getSourceId(), in.getTargetId(), ml,
        null, ""));
  }

  // The filtration flask the funnel sits on, or null.
  private static FiltrationFlask flaskUnder(Bench bench, String funnelId) {
    for (LabItem item : bench.getItems()) {
      if (item.getType() == ItemType.FILTRATION_FLASK
          && funnelId.equals(((FiltrationFlask) item).getFunnelId())) {
        return (FiltrationFlask) item;
      }
    }
    return null;
  }

  // How much of the drug is in a mixture (by B3 it's the only solid).
  private static double drug(Mixture mixture) {
    return mixture.amountOf(Substances.ASPIRIN) + mixture.amountOf(Substances.MEFENAMIC_ACID);
  }

  // After a change: the steps and mistake rules see the action, the lab is saved, and the
  // screen gets the whole lab (every item's PNG facts, the step, Next locked or not).
  private void finish(Lab lab, Action action) {
    lab.record(action);
    presenter.prepareSuccessView(new TransferContentsOutputData(LabStateMapper.from(lab)));
    try {
      dataAccess.saveLab(lab);
    } catch (UncheckedIOException e) {
      // the change already happened; only writing the save file failed, so don't redo it
      presenter.prepareFailView("Done, but your progress couldn't be saved.");
    }
  }

  // Bench lookups: each returns null (or 0) when the item isn't there or isn't that kind.

  // The mixture inside a container (tube, cylinder, pipette, beaker).
  private static Mixture mixtureOf(Bench bench, String id) {
    LabItem item = bench.getItem(id);
    if (item instanceof Vessel) {                     // any container: tube, cylinder, pipette...
      return ((Vessel) item).getMixture();
    }
    return null;
  }

  // What a stock bottle (NaOH, HCl, water, ether) holds.
  private static Substance substanceOf(Bench bench, String id) {
    ItemType type = typeOf(bench, id);
    if (type == ItemType.DROPPER || type == ItemType.SOLVENT_BOTTLE) {
      return ((StockBottle) bench.getItem(id)).getSubstance();
    }
    return null;
  }

  private static ItemType typeOf(Bench bench, String id) {
    LabItem item = bench.getItem(id);
    if (item == null) {
      return null;
    }
    return item.getType();
  }

  // The most a container holds, in mL.
  private static double capacityOf(Bench bench, String id) {
    LabItem item = bench.getItem(id);
    if (item instanceof Vessel) {
      return ((Vessel) item).getCapacity();
    }
    return 0;
  }

  private static PasteurPipette pipetteOf(Bench bench, String id) {
    if (typeOf(bench, id) == ItemType.PIPETTE) {
      return (PasteurPipette) bench.getItem(id);
    }
    return null;
  }
}