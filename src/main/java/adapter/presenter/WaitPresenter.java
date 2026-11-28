package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.wait.WaitOutputBoundary;
import usecase.wait.WaitOutputData;

/**
 * Turns the result of a wait into what the bench shows: the whole lab after it, or an error.
 */
public class WaitPresenter implements WaitOutputBoundary {
  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public WaitPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(WaitOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);                // e.g. "There's nothing to wait for yet."
    viewModel.firePropertyChanged();
  }
}
