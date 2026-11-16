package usecase.answercheckpoint;

/**
 * Receives the result of checking a checkpoint answer.
 */
public interface AnswerCheckpointOutputBoundary {

  /**
   * Shows whether the answer was right.
   *
   * @param out the result
   */
  void prepareSuccessView(AnswerCheckpointOutputData out);

  /**
   * Shows why the answer couldn't be checked.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
