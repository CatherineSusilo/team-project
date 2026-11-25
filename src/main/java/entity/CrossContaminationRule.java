package entity;

/**
 * Catches a pipette that has held both water and ether, because the lab manual says to use a
 * separate pipette for each.
 */
public final class CrossContaminationRule implements MistakeRule {

  // TODO (Lily): once TransferContents calls Lab.record, also flag a used pipette mixing a tube
  //  (A5 needs a new pipette, A7 the same one) using PasteurPipette.canMix, and B4's new dry
  //  pipette.

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.MOVE_LIQUID) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (holdsBothSolvents(source) || holdsBothSolvents(target)) {
      return "That pipette has had both water and ether in it. Undo and use a separate "
          + "pipette for each.";
    }
    return null;
  }

  private static boolean holdsBothSolvents(LabItem item) {
    if (!(item instanceof PasteurPipette)) {
      return false;
    }
    boolean water = false;
    boolean ether = false;
    for (Layer layer : ((PasteurPipette) item).getMixture().getLayers()) {
      water = water || layer.getPhase() == Phase.AQUEOUS && layer.getVolume() > 0;
      ether = ether || layer.getPhase() == Phase.ORGANIC && layer.getVolume() > 0;
    }
    return water && ether;
  }
}
