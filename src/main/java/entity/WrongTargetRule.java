package entity;

import java.util.Optional;

/**
 * Catches a substance added to the wrong vessel: the current step says where it goes and the
 * student's latest transfer put it somewhere else.
 */
public final class WrongTargetRule implements MistakeRule {

  @Override
  public Optional<String> check(Lab lab) {
    Optional<Transfer> last = lab.lastTransfer();
    if (!last.isPresent()) {
      return Optional.empty();
    }
    Substance added = last.get().getSubstance();
    String required = lab.getCurrentStep().requiredTargets().get(added);
    if (required != null && !required.equals(last.get().getTarget())) {
      return Optional.of(added.getName() + " goes in the " + required + ".");
    }
    return Optional.empty();
  }
}
