package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.useitem.UseItemOutputBoundary;
import usecase.useitem.UseItemOutputData;

/**
 * Turns the result of using an item into what the bench shows: the whole lab after it, or
 * an error.
 */
public class UseItemPresenter implements UseItemOutputBoundary {
  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public UseItemPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(UseItemOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);                    // e.g. "That won't fit."; nothing else changes
    viewModel.firePropertyChanged();
  }
}
