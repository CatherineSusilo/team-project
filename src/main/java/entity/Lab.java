package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A student's run through a lab: its steps, the mistake rules, and what they have done so far.
 */
public class Lab {
  // TODO: add the UML's remaining fields (bench, notebook, unknown) and copy() for undo (US-14).
  private final List<LabStep> steps;
  private final List<MistakeRule> rules;
  private final List<Transfer> transfers = new ArrayList<>();
  private int currentIndex;

  /**
   * Creates a lab with no steps and no rules.
   */
  public Lab() {
    this(List.of(), List.of());
  }

  /**
   * Creates a lab.
   *
   * @param steps the steps in order
   * @param rules the mistake rules, checked in order before each step
   */
  public Lab(List<LabStep> steps, List<MistakeRule> rules) {
    this.steps = List.copyOf(steps);
    this.rules = List.copyOf(rules);
  }

  public List<LabStep> getSteps() {
    return steps;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  /**
   * The step the student is on.
   *
   * @return the current step
   * @throws IllegalStateException if the lab has no steps
   */
  public LabStep getCurrentStep() {
    if (steps.isEmpty()) {
      throw new IllegalStateException("This lab has no steps.");
    }
    return steps.get(currentIndex);
  }

  /**
   * Remembers a transfer the student just made.
   *
   * @param transfer what moved where
   */
  public void recordTransfer(Transfer transfer) {
    transfers.add(transfer);
  }

  public List<Transfer> getTransfers() {
    return Collections.unmodifiableList(transfers);
  }

  /**
   * The most recent transfer, which is the one a mistake rule is judging.
   *
   * @return the last transfer, or empty if there is none
   */
  public Optional<Transfer> lastTransfer() {
    if (transfers.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(transfers.get(transfers.size() - 1));
  }

  /**
   * Checks the student's work. Mistake rules run first, so the first rule that fires makes the
   * result a mistake even if the step itself would have been complete.
   *
   * @return the result to show the student
   * @throws IllegalStateException if the lab has no steps
   */
  public StepResult evaluateCurrentStep() {
    if (steps.isEmpty()) {
      throw new IllegalStateException("This lab has no steps.");
    }
    for (MistakeRule rule : rules) {
      Optional<String> mistake = rule.check(this);
      if (mistake.isPresent()) {
        return StepResult.mistake(mistake.get());
      }
    }
    return getCurrentStep().evaluate(this);
  }

  /**
   * Moves to the next step, only if this one is complete.
   *
   * @return true if it moved
   */
  public boolean advance() {
    if (steps.isEmpty()) {
      return false;
    }
    boolean complete = evaluateCurrentStep().getStatus() == StepStatus.COMPLETE;
    if (complete && currentIndex < steps.size() - 1) {
      currentIndex++;
      return true;
    }
    return false;
  }

  /**
   * Moves back one step.
   *
   * @return true if it moved
   */
  public boolean goBack() {
    if (currentIndex > 0) {
      currentIndex--;
      return true;
    }
    return false;
  }
}
