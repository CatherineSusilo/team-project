package usecase.loaddashboard;

/**
 * Receives the dashboard's modules.
 */
public interface LoadDashboardOutputBoundary {

  /**
   * Shows the loaded modules.
   *
   * @param out the modules
   */
  void prepareSuccessView(LoadDashboardOutputData out);

  /**
   * Shows why the modules couldn't be shown.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
