package adapter.controller;

import usecase.loaddashboard.LoadDashboardInputBoundary;
import usecase.loaddashboard.LoadDashboardInputData;

/**
 * Asks for the dashboard's modules to be (re)loaded.
 */
public class LoadDashboardController {
  private final LoadDashboardInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor loads the modules
   */
  public LoadDashboardController(LoadDashboardInputBoundary interactor) {
    this.interactor = interactor;
  }

  /** Loads every module onto the dashboard. */
  public void execute() {
    interactor.execute(new LoadDashboardInputData());
  }
}
