package entity;

/**
 * Catches pressing Tare with the unknown already in the tube. The scale would read 0 for what
 * is in the tube, so the start mass used later for the % recovery would be wrong.
 */
public final class TareRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.USE || action.getOperation() != Operation.TARE) {
      return null;
    }
    LabItem item = lab.getBench().getItem(action.getSourceId());
    if (!(item instanceof Scale)) {
      return null;
    }
    Scale scale = (Scale) item;
    Vessel placed = vessel(lab, scale.getPlacedId());
    Vessel held = placed == null ? null : vessel(lab, placed.getHoldingId());
    if (holdsSample(placed) || holdsSample(held)) {
      return "Tare the scale with the empty tube first.";
    }
    return null;
  }

  private static Vessel vessel(Lab lab, String id) {
    LabItem item = id == null ? null : lab.getBench().getItem(id);
    return item instanceof Vessel ? (Vessel) item : null;
  }

  private static boolean holdsSample(Vessel vessel) {
    if (vessel == null) {
      return false;
    }
    Mixture mixture = vessel.getMixture();
    return mixture.amountOf(Substances.ASPIRIN) > 0
        || mixture.amountOf(Substances.MEFENAMIC_ACID) > 0
        || mixture.amountOf(Substances.VANILLIN) > 0;
  }
}
