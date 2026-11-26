package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.LabViewModel;
import adapter.viewmodel.ModuleState;
import data.Lab3Definition;
import entity.ApiType;
import entity.UnknownSample;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.LabStateData;
import usecase.LabStateMapper;
import usecase.resetmodule.ResetModuleOutputData;

class ResetModulePresenterTest {
  private final LabViewModel labViewModel = new LabViewModel();
  private final DashboardViewModel dashboardViewModel = new DashboardViewModel();
  private final ResetModulePresenter presenter =
      new ResetModulePresenter(labViewModel, dashboardViewModel);
  private final List<ModuleState> modules = List.of(new ModuleState("Pre-lab", "", "", false,
      "", List.of(), List.of(), List.of(), List.of(), ""));
  private int labFired;
  private int dashboardFired;

  ResetModulePresenterTest() {
    dashboardViewModel.setState(modules, "old error");
    labViewModel.addPropertyChangeListener(event -> labFired++);
    dashboardViewModel.addPropertyChangeListener(event -> dashboardFired++);
  }

  @Test
  void successShowsTheFreshLabAndClearsTheMessage() {
    LabStateData state = LabStateMapper.from(
        Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN)));

    presenter.prepareSuccessView(new ResetModuleOutputData("Pre-lab", state));

    assertEquals(0, labViewModel.getStepIndex());
    assertEquals(state.getTitle(), labViewModel.getTitle());
    assertEquals(1, labFired);
    assertEquals("", dashboardViewModel.getMessage(), "an old error doesn't block the reload");
    assertEquals(modules, dashboardViewModel.getModules());
    assertEquals(1, dashboardFired);
  }

  @Test
  void failureShowsTheErrorAndKeepsTheCards() {
    presenter.prepareFailView("Couldn't reset your progress. Please try again.");

    assertEquals("Couldn't reset your progress. Please try again.",
        dashboardViewModel.getMessage());
    assertEquals(modules, dashboardViewModel.getModules());
    assertEquals(1, dashboardFired);
    assertEquals(0, labFired, "the lab screen is left as it was");
  }
}
