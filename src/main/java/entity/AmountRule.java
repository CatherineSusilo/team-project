package entity;

import java.util.Locale;

/**
 * Catches too much liquid in a container: the total goes over the most the current step allows.
 * Too little is not a mistake, the step is just not finished yet.
 */
public final class AmountRule implements MistakeRule {

  // Allows for rounding in amounts such as drops (40 drops of 0.05 mL).
  private static final double TOLERANCE = 1e-9;

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD && action.getKind() != ActionKind.MOVE_LIQUID) {
      return null;
    }
    Range allowed = lab.currentStep().amountRange();
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (allowed == null || !(target instanceof Vessel)) {
      return null;
    }
    double total = ((Vessel) target).getMixture().totalVolume();
    if (total <= allowed.getHi() + TOLERANCE) {
      return null;
    }
    return String.format(Locale.ROOT, "That's too much: %.1f mL, and the most is %.1f mL. "
        + "Undo and add less.", total, allowed.getHi());
  }
}
