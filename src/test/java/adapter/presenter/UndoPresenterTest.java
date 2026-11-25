package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.LabViewModel;
import data.Lab3Definition;
import entity.ApiType;
import entity.UnknownSample;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.undo.UndoOutputData;

class UndoPresenterTest {

  private final LabViewModel viewModel = new LabViewModel();
  private final UndoPresenter presenter = new UndoPresenter(viewModel);

  @Test
  void aSuccessfulUndoUpdatesTheViewModelState() {
    presenter.prepareSuccessView(new UndoOutputData(LabStateMapper.from(
        Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN)))));

    assertEquals(0, viewModel.getStepIndex());
    assertEquals(17, viewModel.getStepCount());
  }

  @Test
  void aFailedUndoShowsTheMessage() {
    presenter.prepareFailView("Nothing to undo.");

    assertEquals("Nothing to undo.", viewModel.getMessage());
  }
}
