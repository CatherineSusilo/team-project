package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.savenotebook.SaveNotebookOutputData;

class SaveNotebookPresenterTest {
  private final DashboardViewModel viewModel = new DashboardViewModel();
  private final SaveNotebookPresenter presenter = new SaveNotebookPresenter(viewModel);
  private final List<ModuleState> modules = List.of(new ModuleState("Pre-lab", "", "", false,
      "", List.of(), List.of(), List.of(), List.of(), ""));
  private int fired;

  SaveNotebookPresenterTest() {
    viewModel.setState(modules, "old note");
    viewModel.addPropertyChangeListener(event -> fired++);
  }

  @Test
  void successClearsTheMessageAndKeepsTheCards() {
    presenter.prepareSuccessView(new SaveNotebookOutputData("Pre-lab"));
    assertEquals("", viewModel.getMessage());
    assertEquals(modules, viewModel.getModules());
    assertEquals(1, fired);
  }

  @Test
  void failureShowsTheErrorAndKeepsTheCards() {
    presenter.prepareFailView("There is no module called Pre-lab 9.");
    assertEquals("There is no module called Pre-lab 9.", viewModel.getMessage());
    assertEquals(modules, viewModel.getModules());
    assertEquals(1, fired);
  }
}
