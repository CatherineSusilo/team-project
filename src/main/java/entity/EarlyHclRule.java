package entity;

/**
 * Catches HCl added before Task B: the student has to add the NaOH first (A6), and the HCl only
 * comes in at B1. It is asked before {@link WrongTargetRule}, whose "HCl goes into the aqueous
 * test tube" would otherwise be shown for HCl poured into the conical tube.
 */
public final class EarlyHclRule implements MistakeRule {

  private final int firstStepWithHcl;

  /**
   * Creates the rule.
   *
   * @param firstStepWithHcl the index of the first Task B step (B1), where HCl is first allowed
   */
  public EarlyHclRule(int firstStepWithHcl) {
    this.firstStepWithHcl = firstStepWithHcl;
  }

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD || lab.getCurrentIndex() >= firstStepWithHcl) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    if (source instanceof StockBottle
        && ((StockBottle) source).getSubstance() == Substances.HCL) {
      return "HCl is added in Task B. Undo and add NaOH first.";
    }
    return null;
  }
}
