package usecase.savequiz;

/**
 * Receives the result of saving quiz answers.
 */
public interface SaveQuizOutputBoundary {

  /**
   * Lets the student leave now that their answers are saved.
   *
   * @param out the saved module
   */
  void prepareSuccessView(SaveQuizOutputData out);

  /**
   * Shows why the answers couldn't be saved.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
