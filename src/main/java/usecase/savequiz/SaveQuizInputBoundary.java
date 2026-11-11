package usecase.savequiz;

/**
 * Saves the student's quiz answers so far, right or wrong.
 */
public interface SaveQuizInputBoundary {

  /**
   * Saves the answers.
   *
   * @param in the student's answers
   */
  void execute(SaveQuizInputData in);
}
