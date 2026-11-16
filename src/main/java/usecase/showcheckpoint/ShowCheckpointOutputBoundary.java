package usecase.showcheckpoint;

/**
 * Receives the checkpoint question to show.
 */
public interface ShowCheckpointOutputBoundary {

  /**
   * Shows the question.
   *
   * @param out the question to ask
   */
  void prepareSuccessView(ShowCheckpointOutputData out);

  /**
   * Shows why there is no question.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
