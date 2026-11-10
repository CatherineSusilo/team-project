package usecase.submitquiz;

/**
 * Submits the student's quiz answers and saves their progress.
 */
public interface SubmitQuizInputBoundary {

  /**
   * Saves progress if every answer is correct.
   *
   * @param in the student's answers
   */
  void execute(SubmitQuizInputData in);
}
