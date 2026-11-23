package adapter.presenter;

import adapter.viewmodel.TransferContentsViewModel;
import usecase.transfercontents.TransferContentsOutputBoundary;
import usecase.transfercontents.TransferContentsOutputData;

/**
 * Turns a transfer result into what the bench shows: the changed container, or an error.
 */
public class TransferContentsPresenter implements TransferContentsOutputBoundary {
  private final TransferContentsViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the bench's transfer view model
   */
  public TransferContentsPresenter(TransferContentsViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(TransferContentsOutputData out) {
    viewModel.setResult(out.getTargetId(), out.getLayerPhases(),
        out.hasSolid(), out.isSettled(), out.getPh(), out.getMessage());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setError(error);
    viewModel.firePropertyChanged();
  }
}
