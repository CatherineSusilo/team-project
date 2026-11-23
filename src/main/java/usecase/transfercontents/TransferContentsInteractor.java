package usecase.transfercontents;

import entity.ChemistryEngine;
import entity.ItemType;
import entity.Layer;
import entity.Mixture;
import entity.StockBottle;
import entity.Substance;
import entity.Substances;
import entity.UnknownSample;
import java.util.ArrayList;
import java.util.List;
import usecase.TransferContentsDataAccessInterface;

/**
 * Carries out a transfer: checks the combination is one from the lab manual, then lets the
 * chemistry engine add it (or mixes the container), and shows the result. Anything not in the
 * lab manual (e.g. HCl into the ether cylinder) is rejected and nothing changes.
 */
public class TransferContentsInteractor implements TransferContentsInputBoundary {
  private final TransferContentsDataAccessInterface dataAccess;
  private final TransferContentsOutputBoundary presenter;
  private final ChemistryEngine chemistry;

  /**
   * Creates the interactor.
   *
   * @param dataAccess finds the items on the bench
   * @param presenter receives the result
   * @param chemistry applies the chemistry
   */
  public TransferContentsInteractor(TransferContentsDataAccessInterface dataAccess,
      TransferContentsOutputBoundary presenter, ChemistryEngine chemistry) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
    this.chemistry = chemistry;
  }

  @Override
  public void execute(TransferContentsInputData in) {
    // get the mixture in the target container, check if it exists and the amount is valid
    Mixture target = dataAccess.getMixture(in.getTargetId());
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
      target.mix((int) Math.round(in.getAmount())); 
      presenter.prepareSuccessView(describe(in.getTargetId(), target, "Mixed."));
      return;
    }

    // the unknown sample: a spatula scoop of grams goes into the conical tube (A4)
    UnknownSample unknown = dataAccess.getUnknown(in.getSourceId());
    if (unknown != null) {
      if (dataAccess.getType(in.getTargetId()) != ItemType.CONICAL_TUBE) {
        presenter.prepareFailView("The unknown goes into the conical tube.");
        return;
      }
      unknown.addTo(target, in.getAmount());
      presenter.prepareSuccessView(describe(in.getTargetId(), target, "Added the unknown."));
      return;
    }

    // everything else is a liquid: drops become mL (10 drops = 0.5 mL)
    double ml;
    if (in.getUnit() == TransferUnit.DROPS) {
      ml = in.getAmount() * StockBottle.DROP_ML;
    } else {
      ml = in.getAmount();
    }
    // it has to fit, e.g. a pipette holds 2 mL
    // (the tiny 1e-9 allows for rounding: 40 drops of 0.05 mL add up to 2.000000000000001)
    if (target.totalVolume() + ml > dataAccess.getCapacity(in.getTargetId()) + 1e-9) {
      presenter.prepareFailView("That won't fit.");
      return;
    }

    // otherwise, the student is transferring a substance into a container, check if it is allowed
    Substance added = dataAccess.getSubstance(in.getSourceId());
    if (added != null) {
      // a stock bottle (NaOH, HCl, water, ether)
      // check if the substance is allowed (by the lab manual) to be added to the target container
      ItemType targetType = dataAccess.getType(in.getTargetId());
      if (!isAllowed(added, targetType)) {
        presenter.prepareFailView(whereItGoes(added));
        return;
      }

      // after checking everything is valid, add the substance to the mixture and return success
      chemistry.addReagent(target, added, ml);
      presenter.prepareSuccessView(describe(in.getTargetId(), target,
          "Added " + added.getName() + "."));
      return;
    }

    // a container (cylinder, tube, pipette): the pipette draws from the bottom and empties
    Mixture source = dataAccess.getMixture(in.getSourceId());
    // only if it actually holds liquid (a tube with just powder has nothing to pour)
    if (source != null && source.totalVolume() > 0) {
      if (!usesPipette(in.getSourceId(), in.getTargetId())) {
        presenter.prepareFailView("Use a pipette to move liquid.");
        return;
      }
      target.pour(source.drawFromBottom(ml));
      presenter.prepareSuccessView(describe(in.getTargetId(), target, "Moved the liquid."));
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
  private boolean usesPipette(String sourceId, String targetId) {
    return dataAccess.getType(sourceId) == ItemType.PIPETTE
        || dataAccess.getType(targetId) == ItemType.PIPETTE;
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

  // Copies what the screen needs out of the mixture.
  private TransferContentsOutputData describe(String targetId, Mixture mixture, String message) {
    List<String> phases = new ArrayList<>();
    boolean hasSolid = false;
    // go through the tube's layers, and record their phases and whether there is undissolved solid
    for (Layer layer : mixture.getLayers()) {
      phases.add(layer.getPhase().name());
      if (!layer.getSolids().isEmpty()) {
        hasSolid = true;
      }
    }
    // based on the targetId, phases, whether it has a solid and the pH
    // the output data tells the presenter what png to show on the screen
    return new TransferContentsOutputData(targetId, phases, hasSolid, mixture.getPh(),
        message);
  }
}
