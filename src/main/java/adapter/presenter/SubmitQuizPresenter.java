package adapter.presenter;

import adapter.viewmodel.QuizViewModel;
import java.util.List;
import usecase.submitquiz.SubmitQuizOutputBoundary;
import usecase.submitquiz.SubmitQuizOutputData;

/**
 * Tells the quiz page whether the student can move on.
 */
public class SubmitQuizPresenter implements SubmitQuizOutputBoundary {
  private final QuizViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the quiz page's view model
   */
  public SubmitQuizPresenter(QuizViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SubmitQuizOutputData out) {
    // No feedback: the labels already show the last check, and the page is about to change.
    viewModel.setState(List.of(), false, true, false, "");
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(List.of(), false, false, false, error);
    viewModel.firePropertyChanged();
  }
}
