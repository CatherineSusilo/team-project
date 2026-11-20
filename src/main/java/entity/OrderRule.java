package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Catches a substance added too early: the student's latest transfer is something that a later
 * step has them add, and the current step does not.
 */
public final class OrderRule implements MistakeRule {
  private final List<LabStep> steps;

  /**
   * Creates the rule.
   *
   * @param steps every step of the lab, in order
   */
  public OrderRule(List<LabStep> steps) {
    this.steps = List.copyOf(steps);
  }

  @Override
  public Optional<String> check(Lab lab) {
    Optional<Transfer> last = lab.lastTransfer();
    if (!last.isPresent()) {
      return Optional.empty();
    }
    Substance added = last.get().getSubstance();
    LabStep current = lab.getCurrentStep();
    if (current.addedSubstances().contains(added)) {
      return Optional.empty();
    }
    for (int i = lab.getCurrentIndex() + 1; i < steps.size(); i++) {
      LabStep later = steps.get(i);
      if (later.addedSubstances().contains(added)) {
        return Optional.of(added.getFormula() + " is added in " + later.getTitle()
            + ". Undo and " + whatToDoFirst(current) + " first.");
      }
    }
    return Optional.empty();
  }

  // What the student should do before the early substance: add this step's own substances.
  private String whatToDoFirst(LabStep current) {
    Set<Substance> own = current.addedSubstances();
    if (own.isEmpty()) {
      return "finish " + current.getTitle();
    }
    List<String> formulas = new ArrayList<>();
    for (Substance substance : own) {
      formulas.add(substance.getFormula());
    }
    Collections.sort(formulas);
    return "add " + String.join(" and ", formulas);
  }
}
