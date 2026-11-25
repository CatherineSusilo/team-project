package entity;

/**
 * Catches ether reaching the aqueous test tube, the one the student labelled "aqueous". Only the
 * water layer is meant to go there.
 */
public final class EtherInAqueousRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD && action.getKind() != ActionKind.MOVE_LIQUID) {
      return null;
    }
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (!(target instanceof Vessel) || !((Vessel) target).labelMatches("aqueous")) {
      return null;
    }
    for (Layer layer : ((Vessel) target).getMixture().getLayers()) {
      if (layer.getPhase() == Phase.ORGANIC && layer.getVolume() > 0) {
        return "Ether got into the aqueous test tube. Undo and move only the water layer.";
      }
    }
    return null;
  }
}
