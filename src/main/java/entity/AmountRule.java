package entity;

import java.util.Locale;

/**
 * Catches too much of what a step measures in a container: the amount goes over the most the
 * current step allows. Too little is not a mistake, the step is just not finished yet.
 */
public final class AmountRule implements MistakeRule {

  // Allows for rounding in amounts such as drops (40 drops of 0.05 mL).
  private static final double TOLERANCE = 1e-9;

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD && action.getKind() != ActionKind.MOVE_LIQUID) {
      return null;
    }
    LabStep step = partFor(lab.currentStep(), action);
    Range allowed = step.amountRange();
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (allowed == null || !(target instanceof Vessel)) {
      return null;
    }
    double amount = measure(((Vessel) target).getMixture(), step.amountSubstance());
    if (amount <= allowed.getHi() + TOLERANCE) {
      return null;
    }
    return String.format(Locale.ROOT, "That's too much: %.1f mL, and the most is %.1f mL. "
        + "Undo and add less.", amount, allowed.getHi());
  }

  // A multi-part step has no range of its own; the part this action belongs to does.
  private static LabStep partFor(LabStep step, Action action) {
    if (step instanceof MultiPartStep) {
      for (LabStep part : ((MultiPartStep) step).getParts()) {
        if (part.expects(action) && part.amountRange() != null) {
          return part;
        }
      }
    }
    return step;
  }

  // What the step is about: one solvent's layer, a reagent, or (no substance) everything.
  private static double measure(Mixture mixture, Substance substance) {
    if (substance == null) {
      return mixture.totalVolume();
    }
    if (substance == Substances.WATER) {
      return layerVolume(mixture, Phase.AQUEOUS);
    }
    if (substance == Substances.ETHER) {
      return layerVolume(mixture, Phase.ORGANIC);
    }
    return mixture.amountOf(substance);
  }

  private static double layerVolume(Mixture mixture, Phase phase) {
    double total = 0;
    for (Layer layer : mixture.getLayers()) {
      if (layer.getPhase() == phase) {
        total += layer.getVolume();
      }
    }
    return total;
  }
}
