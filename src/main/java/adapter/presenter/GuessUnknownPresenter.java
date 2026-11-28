package adapter.presenter;

import adapter.viewmodel.LabViewModel;
import usecase.guessunknown.GuessUnknownOutputBoundary;
import usecase.guessunknown.GuessUnknownOutputData;

/**
 * Turns the result of the guess into what the lab screen shows: the whole lab after a right
 * pick, or the hint after a wrong one.
 */
public class GuessUnknownPresenter implements GuessUnknownOutputBoundary {
  private final LabViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the lab screen's view model
   */
  public GuessUnknownPresenter(LabViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(GuessUnknownOutputData out) {
    viewModel.setState(out.getState());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setMessage(error);                // e.g. the hint comparing the melting points
    viewModel.firePropertyChanged();
  }
}
