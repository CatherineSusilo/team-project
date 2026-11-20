package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;

class NotebookListPanelTest {

  @Test
  void listsEachModuleAndOpensTheOneClicked() {
    NotebookListPanel panel = new NotebookListPanel(List.of("Pre-lab 3", "Lab 4"));
    assertTrue(texts(panel).containsAll(List.of("Your Lab Notebooks", "Pre-lab 3", "Lab 4")));
    assertNull(find(panel, NotebookPanel.class));

    button(panel, "Lab 4").doClick();
    assertNotNull(find(panel, NotebookPanel.class), "the notebook opens, ready to edit");
    assertTrue(texts(panel).contains("Lab 4"));

    button(panel, "← All notebooks").doClick();
    assertNull(find(panel, NotebookPanel.class));
    assertTrue(texts(panel).contains("Pre-lab 3"));
  }

  @Test
  void noModulesShowsAMessage() {
    List<String> texts = texts(new NotebookListPanel(List.of()));
    assertTrue(texts.contains("No notebooks yet. Your notebook starts when you begin the lab."));
    assertEquals(2, texts.size(), "just the heading and the message");
  }

  private static <T> T find(Container root, Class<T> type) {
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) {
        return type.cast(child);
      }
      if (child instanceof Container) {
        T found = find((Container) child, type);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static AbstractButton button(Container root, String text) {
    for (Component child : root.getComponents()) {
      if (child instanceof AbstractButton && text.equals(((AbstractButton) child).getText())) {
        return (AbstractButton) child;
      }
      if (child instanceof Container) {
        AbstractButton found = button((Container) child, text);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static List<String> texts(Container root) {
    List<String> texts = new ArrayList<>();
    for (Component child : root.getComponents()) {
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
