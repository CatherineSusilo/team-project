package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.LoadDashboardController;
import adapter.presenter.LoadDashboardPresenter;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.PartState;
import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import java.awt.Component;
import java.awt.Container;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import usecase.loaddashboard.LoadDashboardInteractor;

class NotStartedModuleTest {
  private static final LabModule NOT_STARTED =
      new LabModule("Pre-lab 3", LocalDate.of(2026, 11, 30), "overview", List.of(),
          List.of(), null, List.of());

  @Test
  void cardShowsNoProgress() {
    DashboardViewModel viewModel = new DashboardViewModel();
    LoadDashboardController controller = new LoadDashboardController(new LoadDashboardInteractor(
        new InMemoryLabDataAccessObject(List.of(NOT_STARTED)),
        new LoadDashboardPresenter(viewModel, Clock.systemDefaultZone())));
    List<String> texts = texts(new DashboardView(viewModel, controller, null, page -> { }));
    assertTrue(texts.contains("Modules"));
    assertTrue(texts.contains("Progress:"));
    assertTrue(texts.contains("0%"));
  }

  @Test
  void headerShowsCourseAndNotebookLink() {
    List<String> texts = texts(new HeaderBar("Pre-lab 3"));
    assertTrue(texts.contains("CHM136 Virtual Lab"));
    assertTrue(texts.contains("Pre-lab 3"));
    assertTrue(texts.contains("View Lab Notebook"));
  }

  @Test
  void popupShowsProgressRowsAndNoContinue() {
    ModuleState module = new ModuleState("Pre-lab 3", "Nov 30, 2026", "26%", false,
        "overview", List.of("goal"), List.of(new PartState("Pre-Procedure Quiz", "80%"),
            new PartState("Part A", "0%")), List.of(), List.of());
    List<String> texts = texts(new ModulePanel(module, () -> { }, () -> { }));
    assertTrue(texts.contains("Due: Nov 30, 2026"));
    assertTrue(texts.contains("Your Progress"));
    assertTrue(texts.containsAll(List.of("1", "Pre-Procedure Quiz", "80%", "2", "Part A")));
    assertTrue(texts.containsAll(List.of("Reset", "Start")));
    assertFalse(texts.contains("Continue"));
  }

  @Test
  void quizCompletePopupRunsEachChoice() {
    List<String> clicked = new ArrayList<>();
    QuizCompletePanel popup = new QuizCompletePanel(() -> clicked.add("exit"),
        () -> clicked.add("save"), () -> clicked.add("proceed"));
    assertTrue(texts(popup).containsAll(List.of("Good Job!",
        "You completed the Pre-Procedure Quiz!")));
    for (String text : List.of("Exit Module", "Save Progress", "Proceed to Experiment")) {
      button(popup, text).doClick();
    }
    assertEquals(List.of("exit", "save", "proceed"), clicked);
  }

  /** The first button labelled {@code text} inside the component, or null if none. */
  private static AbstractButton button(Container container, String text) {
    for (Component child : container.getComponents()) {
      if (child instanceof AbstractButton && text.equals(((AbstractButton) child).getText())) {
        return (AbstractButton) child;
      }
      AbstractButton inside = child instanceof Container ? button((Container) child, text) : null;
      if (inside != null) {
        return inside;
      }
    }
    return null;
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
