package entity;

/**
 * Catches drawing liquid out of a tube that was mixed and has not had time to separate into two
 * layers yet.
 */
public final class UnsettledDrawRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.MOVE_LIQUID) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    LabItem target = lab.getBench().getItem(action.getTargetId());
    boolean drawing = source instanceof Vessel && target instanceof PasteurPipette;
    if (drawing && !((Vessel) source).getMixture().isSettled()) {
      return "Wait for the layers to separate.";
    }
    return null;
  }
}
