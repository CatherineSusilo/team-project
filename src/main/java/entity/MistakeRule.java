package entity;

/**
 * A rule that spots one kind of mistake in what the student just did. The lab asks every rule
 * after each action, once the bench has changed, so a rule judges the state the student left.
 *
 * <p>To add a new kind of mistake, write a class that implements this interface and register
 * it in the lab's definition. Nothing else has to change.
 */
public interface MistakeRule {

  /**
   * Checks what the student just did for the mistake this rule knows about.
   *
   * @param lab the lab as the action left it
   * @param action what the student just did
   * @return the hint to show the student, or null if there is no mistake
   */
  String check(Lab lab, Action action);
}
