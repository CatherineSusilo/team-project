package adapter.presenter;

import adapter.viewmodel.CheckpointViewModel;
import java.util.List;
import usecase.showcheckpoint.ShowCheckpointOutputBoundary;
import usecase.showcheckpoint.ShowCheckpointOutputData;

/**
 * Puts the checkpoint question on the checkpoint page.
 */
public class ShowCheckpointPresenter implements ShowCheckpointOutputBoundary {
  private final CheckpointViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the checkpoint page's view model
   */
  public ShowCheckpointPresenter(CheckpointViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(ShowCheckpointOutputData out) {
    viewModel.setQuestion(out.getStepIndex(), out.getStepTitle(), out.getPrompt(),
        out.getChoices(), out.isMultipleChoice());
    viewModel.firePropertyChanged(CheckpointViewModel.QUESTION);
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setQuestion(-1, "", "", List.of(), false);
    viewModel.setFeedback(false, "", "", "", error);
    viewModel.firePropertyChanged(CheckpointViewModel.QUESTION);
  }
}
