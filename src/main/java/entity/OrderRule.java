package entity;

/**
 * Catches an action the current step doesn't call for, such as adding a reagent a later step
 * adds. The hint is the current step's own hint, which says what to do now.
 */
public final class OrderRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    LabStep step = lab.currentStep();
    return step.expects(action) ? null : step.getHint();
  }
}
