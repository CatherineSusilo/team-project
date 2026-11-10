package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.LoadDashboardController;
import adapter.presenter.LoadDashboardPresenter;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import java.awt.Component;
import java.awt.Container;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import usecase.loaddashboard.LoadDashboardInteractor;

class NotStartedModuleTest {
  private static final LabModule NOT_STARTED =
      new LabModule("Pre-lab 3", LocalDate.of(2026, 11, 30), "overview", false, null, List.of());

  @Test
  void cardSaysNotStarted() {
    DashboardViewModel viewModel = new DashboardViewModel();
    LoadDashboardController controller = new LoadDashboardController(new LoadDashboardInteractor(
        new InMemoryLabDataAccessObject(List.of(NOT_STARTED)),
        new LoadDashboardPresenter(viewModel)));
    assertTrue(texts(new DashboardView(viewModel, controller, null, page -> { }))
        .contains("Last Modified: NOT STARTED"));
  }

  @Test
  void continueOnlyShowsOnceStarted() {
    assertFalse(texts(panel(false)).contains("Continue"));
    assertTrue(texts(panel(true)).contains("Continue"));
  }

  private static ModulePanel panel(boolean started) {
    ModuleState module = new ModuleState("Pre-lab 3", "2026-11-30", "To do",
        started ? "Nov 5, 2026 at 2:30 PM" : "NOT STARTED", started, "overview", List.of());
    return new ModulePanel(module, () -> { }, () -> { }, () -> { });
  }

  /** Text of every label and button inside the component. */
  private static List<String> texts(Container container) {
    List<String> texts = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (child instanceof JLabel) {
        texts.add(((JLabel) child).getText());
      } else if (child instanceof AbstractButton) {
        texts.add(((AbstractButton) child).getText());
      }
      if (child instanceof Container) {
        texts.addAll(texts((Container) child));
      }
    }
    return texts;
  }
}
