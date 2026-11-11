package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.PartState;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.loaddashboard.LoadDashboardOutputData;
import usecase.loaddashboard.LoadDashboardOutputData.ModuleInfo;

class LoadDashboardPresenterTest {

  @Test
  void showsPercentUntilComplete() {
    ModuleState module = present(80, 33);
    assertEquals("33%", module.getProgress());
    assertFalse(module.isComplete());
    List<PartState> parts = module.getParts();
    assertEquals(3, parts.size());
    assertEquals("Pre-Procedure Quiz", parts.get(0).getTitle());
    assertEquals("80%", parts.get(0).getProgress());
    assertEquals("Part A", parts.get(1).getTitle());
    assertEquals("0%", parts.get(1).getProgress());
    assertEquals("Part B", parts.get(2).getTitle());
  }

  @Test
  void dueDateSaysHowLate() {
    assertEquals("Nov 30, 2026", present(0, 0, LocalDate.of(2026, 11, 30)).getDueDate());
    assertEquals("Nov 30, 2026 (1 day late)",
        present(0, 0, LocalDate.of(2026, 12, 1)).getDueDate());
    assertEquals("Nov 30, 2026 (3 days late)",
        present(0, 0, LocalDate.of(2026, 12, 3)).getDueDate());
  }

  @Test
  void showsCompleteAtOneHundred() {
    ModuleState module = present(100, 100);
    assertEquals("Complete", module.getProgress());
    assertTrue(module.isComplete());
  }

  private static ModuleState present(int quizProgress, int progress) {
    return present(quizProgress, progress, LocalDate.of(2026, 11, 10));
  }

  private static ModuleState present(int quizProgress, int progress, LocalDate today) {
    DashboardViewModel viewModel = new DashboardViewModel();
    Clock clock = Clock.fixed(today.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    new LoadDashboardPresenter(viewModel, clock).prepareSuccessView(new LoadDashboardOutputData(
        List.of(new ModuleInfo("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
            quizProgress, List.of("Part A", "Part B"), List.of(0, 0), progress, null,
            List.of(), List.of()))));
    return viewModel.getModules().get(0);
  }
}
