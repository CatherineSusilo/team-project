package entity;

import java.util.List;

/**
 * Adding a number of drops of a reagent to a tube: 10 drops of NaOH to the conical tube (A6),
 * 20 drops of HCl to the test tube (B1). Done once that many drops are in; more than that is
 * flagged by AmountRule, since this step gives it the range.
 */
public class AddReagentStep extends LabStep {
  // Allows for rounding in drops (20 drops of 0.05 mL).
  private static final double TOLERANCE = 1e-9;

  private final ItemType targetType; 
  private final Substance reagent;
  private final int drops;

  /**
   * Creates the part. It sits inside a multi part step, which has the step's title, text
   * and tray, so the part has none of its own.
   *
   * @param targetType the kind of tube the drops go into, e.g. the test tube
   * @param reagent the reagent, e.g. HCl
   * @param drops how many drops, e.g. 20
   */
  public AddReagentStep(ItemType targetType, Substance reagent, int drops) {
    super(0, "", "", "", List.of(), false);
    this.targetType = targetType;
    this.reagent = reagent;
    this.drops = drops;
  }

  @Override
  public boolean isComplete(Lab lab) {
    for (LabItem item : lab.getBench().getItems()) {
      if (item.getType() == targetType
          && ((Vessel) item).getMixture().amountOf(reagent) >= ml() - TOLERANCE) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean expects(Action action) {
    // taking the bottle out, moving it, and adding the drops
    return action.getKind() == ActionKind.SPAWN || action.getKind() == ActionKind.MOVE
        || action.getKind() == ActionKind.ADD;
  }

  @Override
  public Range amountRange() {
    return new Range(ml(), ml());       // exactly that many drops; more is flagged
  }

  @Override
  public Substance amountSubstance() {
    return reagent;
  }

  // Helper Function
  // The drops in mL (20 drops = 1 mL).
  private double ml() {
    return drops * StockBottle.DROP_ML;
  }
}
