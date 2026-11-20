package usecase;

import entity.Lab;
import entity.StepResult;
import entity.StepStatus;

/**
 * What every lab use case hands to its presenter: where the student is, what the step says and
 * whether they can move on. A mistake travels here as a status of MISTAKE with its message.
 */
public final class LabStateData {
  private final int stepIndex;
  private final int stepCount;
  private final String title;
  private final String instruction;
  private final StepStatus status;
  private final String message;
  private final boolean nextLocked;
  private final boolean prevLocked;

  /**
   * Creates the data.
   *
   * @param stepIndex which step the student is on, counting from 0
   * @param stepCount how many steps there are
   * @param title the step's title
   * @param instruction what the student has to do
   * @param status whether the step is incomplete, complete or has a mistake
   * @param message what to tell the student
   * @param nextLocked whether the Next button must be disabled
   * @param prevLocked whether the Back button must be disabled
   */
  public LabStateData(int stepIndex, int stepCount, String title, String instruction,
      StepStatus status, String message, boolean nextLocked, boolean prevLocked) {
    this.stepIndex = stepIndex;
    this.stepCount = stepCount;
    this.title = title;
    this.instruction = instruction;
    this.status = status;
    this.message = message;
    this.nextLocked = nextLocked;
    this.prevLocked = prevLocked;
  }

  /**
   * Builds the data from the lab and what its current step just reported. Next stays locked
   * until the step is complete, so a mistake always blocks it.
   *
   * @param lab the lab the student is working in
   * @param result what the current step (or a mistake rule) said
   * @return the data for the presenter
   */
  public static LabStateData from(Lab lab, StepResult result) {
    return new LabStateData(
        lab.getCurrentIndex(),
        lab.getSteps().size(),
        lab.getCurrentStep().getTitle(),
        lab.getCurrentStep().getInstruction(),
        result.getStatus(),
        result.getMessage(),
        result.getStatus() != StepStatus.COMPLETE,
        lab.getCurrentIndex() == 0);
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public int getStepCount() {
    return stepCount;
  }

  public String getTitle() {
    return title;
  }

  public String getInstruction() {
    return instruction;
  }

  public StepStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public boolean isNextLocked() {
    return nextLocked;
  }

  public boolean isPrevLocked() {
    return prevLocked;
  }
}
