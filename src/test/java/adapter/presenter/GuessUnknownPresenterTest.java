package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.LabViewModel;
import data.Lab3Definition;
import entity.ApiType;
import entity.Lab;
import entity.UnknownSample;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.guessunknown.GuessUnknownOutputData;

/**
 * Tests that the presenter puts the whole lab on the shared lab view model and redraws once.
 */
class GuessUnknownPresenterTest {
  private final LabViewModel viewModel = new LabViewModel();
  private final GuessUnknownPresenter presenter = new GuessUnknownPresenter(viewModel);
  private int redraws;

  GuessUnknownPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void successShowsTheWholeLab() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    presenter.prepareSuccessView(new GuessUnknownOutputData(LabStateMapper.from(lab)));
    assertEquals(lab.getBench().getItems().size(), viewModel.getItems().size());
    assertEquals(1, redraws);
  }

  @Test
  void wrongGuessShowsTheHint() {
    presenter.prepareFailView("Try again.");
    assertEquals("Try again.", viewModel.getMessage());
    assertEquals(1, redraws);
  }
}
