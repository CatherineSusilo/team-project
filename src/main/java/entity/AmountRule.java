package entity;

import java.util.Locale;

/**
 * Catches too much of what a step measures: the amount goes over the most the current step
 * allows. Too little is not a mistake, the step is just not finished yet. Liquids are measured
 * in the target container; scooping the unknown from its vial is measured by the grams of the
 * unknown now in the target tube, so it doesn't depend on the scale or on taring.
 */
public final class AmountRule implements MistakeRule {

  // Allows for rounding in amounts such as drops (40 drops of 0.05 mL).
  private static final double TOLERANCE = 1e-9;

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() == ActionKind.ADD && isVial(lab, action.getSourceId())) {
      return checkMass(lab, action);
    }
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
    if (isReagent(step.amountSubstance())) {
      // Reagents come in drops, so say it in drops: "11 drops, and the most is 10 drops".
      return String.format(Locale.ROOT, "That's too much: %d drops, and the most is %d drops. "
          + "Undo and add fewer.", Math.round(amount / StockBottle.DROP_ML),
          Math.round(allowed.getHi() / StockBottle.DROP_ML));
    }
    return String.format(Locale.ROOT, "That's too much: %.1f mL, and the most is %.1f mL. "
        + "Undo and add less.", amount, allowed.getHi());
  }

  private static boolean isVial(Lab lab, String itemId) {
    LabItem item = lab.getBench().getItem(itemId);
    return item != null && item.getType() == ItemType.VIAL;
  }

  // Scooping the unknown: the unknown in the target tube (the drug and its vanillin, in grams)
  // may not go over the step's most.
  private static String checkMass(Lab lab, Action action) {
    Range allowed = partFor(lab.currentStep(), action).amountRange();
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (allowed == null || !(target instanceof Vessel)) {
      return null;
    }
    Mixture tube = ((Vessel) target).getMixture();
    double grams = tube.amountOf(Substances.ASPIRIN) + tube.amountOf(Substances.MEFENAMIC_ACID)
        + tube.amountOf(Substances.VANILLIN);
    if (grams > allowed.getHi() + TOLERANCE) {
      return String.format(Locale.ROOT, "Too much: %.3f g", grams);
    }
    return null;
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

  private static boolean isReagent(Substance substance) {
    return substance == Substances.NAOH || substance == Substances.HCL;
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
