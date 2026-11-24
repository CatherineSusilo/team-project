package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.spawnitem.SpawnItemOutputBoundary;
import usecase.spawnitem.SpawnItemOutputData;

/**
 * Writes the result of the SpawnItem use case to the shared lab view model.
 */
public class SpawnItemPresenter implements SpawnItemOutputBoundary {

  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public SpawnItemPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SpawnItemOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);
    viewModel.firePropertyChanged();
  }
}
