package adapter.presenter;

import adapter.viewmodel.DashboardViewModel;
import usecase.savenotebook.SaveNotebookOutputBoundary;
import usecase.savenotebook.SaveNotebookOutputData;

/**
 * Tells the dashboard whether the student's notebook was saved. The module cards stay as they
 * are; reloading the dashboard picks up the saved notebook.
 */
public class SaveNotebookPresenter implements SaveNotebookOutputBoundary {
  private final DashboardViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the dashboard's view model
   */
  public SaveNotebookPresenter(DashboardViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SaveNotebookOutputData out) {
    viewModel.setState(viewModel.getModules(), "");
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(viewModel.getModules(), error);
    viewModel.firePropertyChanged();
  }
}
