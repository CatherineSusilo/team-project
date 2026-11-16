package adapter.presenter;

import adapter.viewmodel.CheckpointViewModel;
import usecase.answercheckpoint.AnswerCheckpointOutputBoundary;
import usecase.answercheckpoint.AnswerCheckpointOutputData;

/**
 * Turns a checkpoint result into the feedback shown under the question.
 */
public class AnswerCheckpointPresenter implements AnswerCheckpointOutputBoundary {
  private final CheckpointViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the checkpoint page's view model
   */
  public AnswerCheckpointPresenter(CheckpointViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(AnswerCheckpointOutputData out) {
    if (out.isCorrect()) {
      viewModel.setFeedback(true, "Correct.", out.getExplanation(), out.getTakeaway(), "");
    } else {
      viewModel.setFeedback(false, "Not quite. Think back to what you just did and try again.",
          "", "", "");
    }
    viewModel.firePropertyChanged(CheckpointViewModel.FEEDBACK);
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setFeedback(false, "", "", "", error);
    viewModel.firePropertyChanged(CheckpointViewModel.FEEDBACK);
  }
}
