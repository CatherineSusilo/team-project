package entity;

/**
 * Catches a conical tube labelled with something other than the student's sample ID (the batch
 * letter). Judges the label as it is after the student wrote it; a blank label is refused
 * before it gets here.
 */
public final class LabelRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.USE || action.getOperation() != Operation.LABEL) {
      return null;
    }
    LabItem item = lab.getBench().getItem(action.getSourceId());
    if (!(item instanceof Vessel) || item.getType() != ItemType.CONICAL_TUBE) {
      return null;
    }
    String batch = String.valueOf(lab.getUnknown().getBatch());
    if (((Vessel) item).labelMatches(batch)) {
      return null;
    }
    return "Label it with your sample ID.";
  }
}
