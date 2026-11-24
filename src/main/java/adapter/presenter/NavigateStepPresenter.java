package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.navigatestep.NavigateStepOutputBoundary;
import usecase.navigatestep.NavigateStepOutputData;

/**
 * Writes the result of the NavigateStep use case to the shared lab view model.
 */
public class NavigateStepPresenter implements NavigateStepOutputBoundary {

  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public NavigateStepPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(NavigateStepOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);
    viewModel.firePropertyChanged();
  }
}
