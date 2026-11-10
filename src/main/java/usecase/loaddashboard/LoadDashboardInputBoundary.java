package usecase.loaddashboard;

/**
 * Loads the modules shown on the dashboard.
 */
public interface LoadDashboardInputBoundary {

  /**
   * Loads every module and passes them to the presenter.
   *
   * @param in the input data
   */
  void execute(LoadDashboardInputData in);
}
