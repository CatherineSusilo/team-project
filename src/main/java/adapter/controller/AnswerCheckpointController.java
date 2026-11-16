package adapter.controller;

import java.util.Set;
import usecase.answercheckpoint.AnswerCheckpointInputBoundary;
import usecase.answercheckpoint.AnswerCheckpointInputData;

/**
 * Passes the student's checkpoint answer on to be checked.
 */
public class AnswerCheckpointController {
  private final AnswerCheckpointInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor checks the answer
   */
  public AnswerCheckpointController(AnswerCheckpointInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Checks the answer.
   *
   * @param stepIndex the index of the step the checkpoint comes after
   * @param selected the indexes of the choices the student selected
   */
  public void execute(int stepIndex, Set<Integer> selected) {
    interactor.execute(new AnswerCheckpointInputData(stepIndex, selected));
  }
}
