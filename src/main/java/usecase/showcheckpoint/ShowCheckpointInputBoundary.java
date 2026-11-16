package usecase.showcheckpoint;

/**
 * Loads the checkpoint question for a finished step.
 */
public interface ShowCheckpointInputBoundary {

  /**
   * Loads the question.
   *
   * @param in which step the student just finished
   */
  void execute(ShowCheckpointInputData in);
}
