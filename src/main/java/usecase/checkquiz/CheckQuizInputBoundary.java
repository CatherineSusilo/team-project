package usecase.checkquiz;

/**
 * Checks the student's quiz answers.
 */
public interface CheckQuizInputBoundary {

  /**
   * Checks the answers, and when submitting, saves progress if every answer is correct.
   *
   * @param in the student's answers
   */
  void execute(CheckQuizInputData in);
}
