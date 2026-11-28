package adapter.controller;

import usecase.wait.WaitInputBoundary;
import usecase.wait.WaitInputData;

/**
 * Tells the lab that time passed. The timer bar calls it once, when it finishes or its Skip
 * button is pressed.
 */
public class WaitController {
  private final WaitInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor lets time pass
   */
  public WaitController(WaitInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Lets the given lab minutes pass.
   *
   * @param minutes how long the timer bar counted, e.g. 1
   */
  public void execute(double minutes) {
    interactor.execute(new WaitInputData(minutes));
  }
}
