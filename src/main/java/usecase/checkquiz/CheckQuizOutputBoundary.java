package usecase.checkquiz;

/**
 * Receives the result of checking a quiz.
 */
public interface CheckQuizOutputBoundary {

  /**
   * Shows how each answer did.
   *
   * @param out the result for every question
   */
  void prepareSuccessView(CheckQuizOutputData out);

  /**
   * Shows why the answers couldn't be checked.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
