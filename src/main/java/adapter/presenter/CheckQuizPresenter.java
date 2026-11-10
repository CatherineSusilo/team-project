package adapter.presenter;

import adapter.viewmodel.FeedbackState;
import adapter.viewmodel.QuizViewModel;
import java.util.ArrayList;
import java.util.List;
import usecase.checkquiz.CheckQuizOutputBoundary;
import usecase.checkquiz.CheckQuizOutputData;
import usecase.checkquiz.CheckQuizOutputData.QuestionResult;

/**
 * Turns quiz results into the feedback shown under each question.
 */
public class CheckQuizPresenter implements CheckQuizOutputBoundary {
  private final QuizViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the quiz page's view model
   */
  public CheckQuizPresenter(QuizViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(CheckQuizOutputData out) {
    List<FeedbackState> feedback = new ArrayList<>();
    for (QuestionResult result : out.results()) {
      if (!result.answered()) {
        // Don't reveal the explanation (and so the answer) for skipped questions.
        feedback.add(new FeedbackState(false, "Not answered yet.", ""));
      } else {
        feedback.add(new FeedbackState(result.correct(),
            result.correct() ? "Correct." : "Not quite.", result.explanation()));
      }
    }
    viewModel.setState(feedback, out.allCorrect(), out.submitted(), "");
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(viewModel.getFeedback(), false, false, error);
    viewModel.firePropertyChanged();
  }
}
