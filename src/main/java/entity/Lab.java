package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A student's run through a lab: its steps, the bench, and how far they have got.
 *
 * <p>The steps are {@code transient}: they hold tests that can't be saved as JSON, so they are
 * attached again from the lab's definition after a restore. Everything the student has done is
 * in the other fields.
 */
public class Lab {

  private transient List<LabStep> steps;
  private int currentIndex;
  private final Bench bench;
  private final Set<Integer> completedSteps = new HashSet<>();
  private String standingMistake;

  /**
   * Creates a lab at its first step.
   *
   * @param steps the lab-manual steps, in order
   * @param bench the starting bench
   */
  public Lab(List<LabStep> steps, Bench bench) {
    this.steps = List.copyOf(steps);
    this.bench = bench;
  }

  /**
   * Puts the steps back after the lab was restored from a save.
   *
   * @param restoredSteps the lab-manual steps, in order
   */
  public void attachSteps(List<LabStep> restoredSteps) {
    this.steps = List.copyOf(restoredSteps);
  }

  /**
   * The step the student is on.
   *
   * @return the current step
   */
  public LabStep currentStep() {
    return steps.get(currentIndex);
  }

  /**
   * Where the student stands on the current step. A step stays complete once it has been
   * done, even if the bench changes afterwards.
   *
   * @return MISTAKE while a mistake is standing, else COMPLETE or INCOMPLETE
   */
  public StepStatus status() {
    if (standingMistake != null) {
      return StepStatus.MISTAKE;
    }
    if (completedSteps.contains(currentIndex) || currentStep().isComplete(this)) {
      return StepStatus.COMPLETE;
    }
    return StepStatus.INCOMPLETE;
  }

  /**
   * What to tell the student about the current step.
   *
   * @return the standing mistake's hint, or the step's own hint
   */
  public String message() {
    return standingMistake != null ? standingMistake : currentStep().getHint();
  }

  /**
   * Notes something the student did: if it finishes the step, the step is marked complete for
   * good.
   *
   * @param action what the student just did
   * @return where the student stands on the current step now
   */
  public StepStatus record(Action action) {
    if (currentStep().isComplete(this)) {
      completedSteps.add(currentIndex);
    }
    return status();
  }

  /**
   * Moves to the next step, only if the current one is complete.
   *
   * @return true if the lab moved on
   */
  public boolean advance() {
    if (status() != StepStatus.COMPLETE || !hasNextStep()) {
      return false;
    }
    completedSteps.add(currentIndex);
    currentIndex++;
    return true;
  }

  /**
   * Moves back one step, which is always allowed. The step moved back to, and every later
   * one, count as not done until the student does them again.
   *
   * @return true if the lab moved back, false if it was on the first step
   */
  public boolean goBack() {
    if (currentIndex == 0) {
      return false;
    }
    currentIndex--;
    int first = currentIndex;
    completedSteps.removeIf(index -> index >= first);
    standingMistake = null;
    return true;
  }

  /**
   * Whether there is a step after the current one.
   *
   * @return true unless the student is on the last step
   */
  public boolean hasNextStep() {
    return currentIndex < steps.size() - 1;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public int getStepCount() {
    return steps.size();
  }

  public List<LabStep> getSteps() {
    return new ArrayList<>(steps);
  }

  public Bench getBench() {
    return bench;
  }

  /**
   * The steps the student has finished.
   *
   * @return a copy of the finished steps' positions
   */
  public Set<Integer> getCompletedSteps() {
    return new HashSet<>(completedSteps);
  }
}
