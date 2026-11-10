package usecase.submitquiz;

/**
 * Receives the result of submitting a quiz.
 */
public interface SubmitQuizOutputBoundary {

  /**
   * Moves the student on now that their progress is saved.
   *
   * @param out the submitted module
   */
  void prepareSuccessView(SubmitQuizOutputData out);

  /**
   * Shows why the quiz couldn't be submitted.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
