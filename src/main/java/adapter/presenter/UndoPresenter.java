package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.undo.UndoOutputBoundary;
import usecase.undo.UndoOutputData;

/**
 * Writes the result of the Undo use case to the shared lab view model.
 */
public class UndoPresenter implements UndoOutputBoundary {

  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public UndoPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(UndoOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);
    viewModel.firePropertyChanged();
  }
}
