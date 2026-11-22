package usecase.transfercontents;

import entity.ChemistryEngine;
import entity.ItemType;
import entity.Layer;
import entity.Mixture;
import entity.Substance;
import entity.Substances;
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
    // get the mixture in the target container, check if it exits and the amount is valid
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

    // otherwise, the student is transferring a substance into a container, check if it is allowed
    Substance added = dataAccess.getSubstance(in.getSourceId());
    // student tried adding something that is empty, return error
    if (added == null) {
      presenter.prepareFailView("There's nothing in that to pour.");
      return;
    }
    // check if the substance is allowed (by the lab manual) to be added to the target container
    ItemType targetType = dataAccess.getType(in.getTargetId());
    if (!isAllowed(added, targetType)) {
      presenter.prepareFailView(whereItGoes(added));          
      return;
    }

    // after checking everything is valid, add the substance to the mixture and return success
    chemistry.addReagent(target, added, in.getAmount());
    presenter.prepareSuccessView(describe(in.getTargetId(), target,
        "Added " + added.getName() + "."));
  }
  
  // Helper functions

  //Whether this is a valid transfer, referring to the la manual for valid transfers.
  private boolean isAllowed(Substance added, ItemType target) {
    if (added == Substances.NAOH) {
      // Task A: NaOH is only allowed in the conical tube
      return target == ItemType.CONICAL_TUBE;             
    } else if (added == Substances.HCL) {
      // Task B: HCl is only allowed in the test tube
      return target == ItemType.TEST_TUBE;            
    } else if (added == Substances.WATER || added == Substances.ETHER) {
      // water and ether are allowed in the conical tube or its graduated cylinder
      return target == ItemType.CONICAL_TUBE || target == ItemType.GRADUATED_CYLINDER;
    } else if (added == Substances.ASPIRIN || added == Substances.MEFENAMIC_ACID
        || added == Substances.VANILLIN) {
      // unknowns are only allowed in the conical tube
      return target == ItemType.CONICAL_TUBE;                
    }
    return false;                                             
  }

  // Extra: Gives a hint to the student after doing something wrong once.
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
    // based on the targetid, phases, whether its a solid and ph
    // the output data tells the presenter what png to show on the screen
    return new TransferContentsOutputData(targetId, phases, hasSolid, mixture.getPh(),
        message);
  }
}
