package usecase.showcheckpoint;

/**
 * Which step the student just finished.
 */
public final class ShowCheckpointInputData {
  private final int stepIndex;

  /**
   * Creates the input.
   *
   * @param stepIndex the index of the step just finished
   */
  public ShowCheckpointInputData(int stepIndex) {
    this.stepIndex = stepIndex;
  }

  public int getStepIndex() {
    return stepIndex;
  }
}
