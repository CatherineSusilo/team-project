package entity;

import java.util.Optional;

/**
 * A rule that spots a mistake the student made, whatever the current step is. A new kind of
 * mistake only needs a new rule; the lab runs every rule before it checks the step itself.
 */
public interface MistakeRule {

  /**
   * Checks the lab for the mistake this rule knows about.
   *
   * @param lab the lab to check
   * @return the message to show the student, or empty if there is no mistake
   */
  Optional<String> check(Lab lab);
}
