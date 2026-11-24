package adapter.controller;

import usecase.navigatestep.NavigateStepInputBoundary;
import usecase.navigatestep.NavigateStepInputData;

/**
 * Passes the student's request to the NavigateStep use case.
 */
public class NavigateStepController {

  private final NavigateStepInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor the use case to call
   */
  public NavigateStepController(NavigateStepInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Moves the student to the next or the previous step.
   *
   * @param forward true for Next, false for Prev
   */
  public void execute(boolean forward) {
    interactor.execute(new NavigateStepInputData(forward));
  }
}
