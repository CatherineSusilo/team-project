package adapter.presenter;

import adapter.viewmodel.QuizViewModel;
import java.util.List;
import usecase.savequiz.SaveQuizOutputBoundary;
import usecase.savequiz.SaveQuizOutputData;

/**
 * Tells the quiz page whether the student's answers were saved, so it can leave.
 */
public class SaveQuizPresenter implements SaveQuizOutputBoundary {
  private final QuizViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the quiz page's view model
   */
  public SaveQuizPresenter(QuizViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SaveQuizOutputData out) {
    // No feedback: the labels already show the last check, and the page is about to change.
    viewModel.setState(List.of(), false, true, "");
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(List.of(), false, false, error);
    viewModel.firePropertyChanged();
  }
}
