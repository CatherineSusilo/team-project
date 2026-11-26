package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.transfercontents.TransferContentsOutputBoundary;
import usecase.transfercontents.TransferContentsOutputData;

/**
 * Turns a transfer result into what the bench shows: the whole lab after it, or an error.
 */
public class TransferContentsPresenter implements TransferContentsOutputBoundary {
  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public TransferContentsPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(TransferContentsOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);                    // e.g. "That won't fit."; nothing else changes
    viewModel.firePropertyChanged();
  }
}
