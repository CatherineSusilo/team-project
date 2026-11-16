package usecase.answercheckpoint;

/**
 * Checks the student's answer to a checkpoint question.
 */
public interface AnswerCheckpointInputBoundary {

  /**
   * Checks the answer.
   *
   * @param in the step and the student's answer
   */
  void execute(AnswerCheckpointInputData in);
}
