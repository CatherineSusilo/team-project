package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.LabViewModel;
import data.Lab3Definition;
import entity.ApiType;
import entity.Lab;
import entity.UnknownSample;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.wait.WaitOutputData;

/**
 * Tests that the presenter puts the whole lab on the shared lab view model and redraws once.
 */
class WaitPresenterTest {
  private final LabViewModel viewModel = new LabViewModel();
  private final WaitPresenter presenter = new WaitPresenter(viewModel);
  private int redraws;

  WaitPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void successShowsTheWholeLab() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    presenter.prepareSuccessView(new WaitOutputData(LabStateMapper.from(lab)));
    assertEquals(lab.getBench().getItems().size(), viewModel.getItems().size());
    assertEquals(0, viewModel.getWaitMinutes());            // A1 has nothing to wait for
    assertEquals(1, redraws);
  }

  @Test
  void failureShowsTheMessage() {
    presenter.prepareFailView("There's nothing to wait for yet.");
    assertEquals("There's nothing to wait for yet.", viewModel.getMessage());
    assertEquals(1, redraws);
  }
}
