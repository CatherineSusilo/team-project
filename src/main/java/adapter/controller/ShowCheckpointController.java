package adapter.controller;

import usecase.showcheckpoint.ShowCheckpointInputBoundary;
import usecase.showcheckpoint.ShowCheckpointInputData;

/**
 * Asks for the checkpoint question that follows a finished step.
 */
public class ShowCheckpointController {
  private final ShowCheckpointInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor loads the question
   */
  public ShowCheckpointController(ShowCheckpointInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Loads the question.
   *
   * @param stepIndex the index of the step just finished
   */
  public void execute(int stepIndex) {
    interactor.execute(new ShowCheckpointInputData(stepIndex));
  }
}
