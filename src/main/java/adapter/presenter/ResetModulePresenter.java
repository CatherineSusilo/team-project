package adapter.presenter;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.LabViewModel;
import usecase.resetmodule.ResetModuleOutputBoundary;
import usecase.resetmodule.ResetModuleOutputData;

/**
 * Shows the result of a reset: the lab screen gets the fresh lab, and the dashboard's message
 * says whether it worked. Reloading the dashboard picks up the cleared progress.
 */
public class ResetModulePresenter implements ResetModuleOutputBoundary {
  private final LabViewModel labViewModel;
  private final DashboardViewModel dashboardViewModel;

  /**
   * Creates the presenter.
   *
   * @param labViewModel the lab screen's view model
   * @param dashboardViewModel the dashboard's view model
   */
  public ResetModulePresenter(LabViewModel labViewModel, DashboardViewModel dashboardViewModel) {
    this.labViewModel = labViewModel;
    this.dashboardViewModel = dashboardViewModel;
  }

  @Override
  public void prepareSuccessView(ResetModuleOutputData out) {
    labViewModel.setState(out.getState());
    labViewModel.firePropertyChanged();
    dashboardViewModel.setState(dashboardViewModel.getModules(), "");
    dashboardViewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    dashboardViewModel.setState(dashboardViewModel.getModules(), error);
    dashboardViewModel.firePropertyChanged();
  }
}
