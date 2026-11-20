package entity;

/**
 * What a step says about the student's work right now: a status and a message to show them.
 */
public final class StepResult {
  private final StepStatus status;
  private final String message;

  private StepResult(StepStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * Creates a result for a finished step.
   *
   * @param message what to tell the student
   * @return the result
   */
  public static StepResult complete(String message) {
    return new StepResult(StepStatus.COMPLETE, message);
  }

  /**
   * Creates a result for a step that is not finished yet.
   *
   * @param message what to tell the student
   * @return the result
   */
  public static StepResult incomplete(String message) {
    return new StepResult(StepStatus.INCOMPLETE, message);
  }

  /**
   * Creates a result for a step the student got wrong. The student sees it in red and can undo.
   *
   * @param message what went wrong and how to fix it
   * @return the result
   */
  public static StepResult mistake(String message) {
    return new StepResult(StepStatus.MISTAKE, message);
  }

  public StepStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
