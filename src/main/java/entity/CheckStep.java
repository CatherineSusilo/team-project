package entity;

import java.util.List;
import java.util.function.Predicate;

/**
 * A one-off step defined by two tests: whether it is done, and which actions belong to it.
 */
public class CheckStep extends LabStep {

  private final Predicate<Lab> done;
  private final Predicate<Action> expected;

  /**
   * Creates a step.
   *
   * @param id the step's position in the lab, from 0
   * @param title a short name
   * @param instruction what the student is asked to do
   * @param hint what to say while the step is not done
   * @param trayTemplates the template ids the tray offers for this step
   * @param optional whether the student may skip it
   * @param done true once the lab satisfies the step
   * @param expected true for the actions that belong to the step
   */
  public CheckStep(int id, String title, String instruction, String hint,
      List<String> trayTemplates, boolean optional, Predicate<Lab> done,
      Predicate<Action> expected) {
    super(id, title, instruction, hint, trayTemplates, optional);
    this.done = done;
    this.expected = expected;
  }

  @Override
  public boolean isComplete(Lab lab) {
    return done.test(lab);
  }

  @Override
  public boolean expects(Action action) {
    return expected.test(action);
  }
}
