package entity;

import java.util.Map;
import java.util.Set;

/**
 * A step for tests that adds the given substances and can restrict where they go.
 */
final class StubStep extends LabStep {
  private final Set<Substance> added;
  private final Map<Substance, String> targets;

  StubStep(String title, Set<Substance> added, Map<Substance, String> targets) {
    super(title, title, "do " + title);
    this.added = added;
    this.targets = targets;
  }

  StubStep(String title, Substance added) {
    this(title, Set.of(added), Map.of());
  }

  @Override
  public StepResult evaluate(Lab lab) {
    return StepResult.incomplete("keep going");
  }

  @Override
  public Set<Substance> addedSubstances() {
    return added;
  }

  @Override
  public Map<Substance, String> requiredTargets() {
    return targets;
  }
}
