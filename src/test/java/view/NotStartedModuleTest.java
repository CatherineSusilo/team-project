package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import java.awt.Component;
import java.awt.Container;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;

class NotStartedModuleTest {
  private static final LabModule NOT_STARTED =
      new LabModule("Pre-lab 3", LocalDate.of(2026, 11, 30), "overview", false, null, List.of());
  private static final LabModule STARTED = new LabModule("Pre-lab 3",
      LocalDate.of(2026, 11, 30), "overview", false, LocalDateTime.of(2026, 11, 5, 14, 30),
      List.of());

  @Test
  void cardSaysNotStarted() {
    assertTrue(texts(new DashboardView(List.of(NOT_STARTED),
        new InMemoryLabDataAccessObject(), page -> { }))
        .contains("Last Modified: NOT STARTED"));
  }

  @Test
  void continueOnlyShowsOnceStarted() {
    assertFalse(texts(panel(NOT_STARTED)).contains("Continue"));
    assertTrue(texts(panel(STARTED)).contains("Continue"));
  }

  private static ModulePanel panel(LabModule module) {
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
