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
  private transient List<MistakeRule> rules = List.of();
  private int currentIndex;
  private final Bench bench;
  private final UnknownSample unknown;
  private final Set<Integer> completedSteps = new HashSet<>();
  private String standingMistake;
  private final LabRecord record = new LabRecord();

  /**
   * Creates a lab at its first step.
   *
   * @param steps the lab-manual steps, in order
   * @param bench the starting bench
   * @param unknown the student's unknown sample
   */
  public Lab(List<LabStep> steps, Bench bench, UnknownSample unknown) {
    this(steps, List.of(), bench, unknown);
  }

  /**
   * Creates a lab at its first step, with rules that flag the student's mistakes.
   *
   * @param steps the lab-manual steps, in order
   * @param rules the mistake rules, asked in order after every action
   * @param bench the starting bench
   * @param unknown the student's unknown sample
   */
  public Lab(List<LabStep> steps, List<MistakeRule> rules, Bench bench, UnknownSample unknown) {
    this.steps = List.copyOf(steps);
    this.rules = List.copyOf(rules);
    this.bench = bench;
    this.unknown = unknown;
  }

  /** An empty lab with no steps, for tests that only need something to save and restore. */
  protected Lab() {
    this(List.of(), new Bench(), null);
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
   * Puts the mistake rules back after the lab was restored from a save.
   *
   * @param restoredRules the mistake rules, asked in order after every action
   */
  public void attachRules(List<MistakeRule> restoredRules) {
    this.rules = List.copyOf(restoredRules);
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
   * Notes something the student did. The mistake rules are asked first, and the first hint
   * becomes the standing mistake. It stays until the student undoes or goes back, even if they
   * then do something right. Then, if the action finishes the step, the step is marked complete
   * for good.
   *
   * @param action what the student just did
   * @return where the student stands on the current step now
   */
  public StepStatus record(Action action) {
    if (standingMistake == null) {
      standingMistake = firstMistake(action);
    }
    if (currentStep().isComplete(this)) {
      completedSteps.add(currentIndex);
    }
    return status();
  }

  // The first rule's hint, or null if no rule objects. A lab restored from a save has no rules
  // until attachRules is called.
  private String firstMistake(Action action) {
    if (rules == null) {
      return null;
    }
    for (MistakeRule rule : rules) {
      String hint = rule.check(this, action);
      if (hint != null) {
        return hint;
      }
    }
    return null;
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
   * Lets time pass on the bench, so everything that depends on it (a mixture settling) moves
   * on. Called when the timer bar finishes or is skipped.
   *
   * @param minutes how many lab minutes have passed, not negative
   * @throws IllegalArgumentException if minutes is negative
   */
  public void advanceTime(double minutes) {
    if (minutes < 0) {
      throw new IllegalArgumentException("Time can't go backwards: " + minutes);
    }
    for (LabItem item : bench.getItems()) {
      if (item instanceof Vessel) {
        ((Vessel) item).getMixture().advance(minutes);
      }
      if (item.getType() == ItemType.ICE_BATH) {
        ((IceBath) item).advance(minutes);            // B2: everything in the bath cools
      }
    }
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

  public UnknownSample getUnknown() {
    return unknown;
  }

  public Bench getBench() {
    return bench;
  }

  /**
   * The results the simulation has measured so far.
   *
   * @return the lab's record, which is saved with the lab
   */
  public LabRecord getRecord() {
    return record;
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
