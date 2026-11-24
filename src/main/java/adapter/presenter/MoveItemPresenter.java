package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.moveitem.MoveItemOutputBoundary;
import usecase.moveitem.MoveItemOutputData;

/**
 * Writes the result of the MoveItem use case to the shared lab view model.
 */
public class MoveItemPresenter implements MoveItemOutputBoundary {

  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public MoveItemPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(MoveItemOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);
    viewModel.firePropertyChanged();
  }
}
