package usecase.checkquiz;

/**
 * Checks the student's quiz answers.
 */
public interface CheckQuizInputBoundary {

  /**
   * Checks the answers without saving anything.
   *
   * @param in the student's answers
   */
  void execute(CheckQuizInputData in);
}
