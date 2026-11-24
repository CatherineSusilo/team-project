package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;

class NotebookListPanelTest {

  @Test
  void listsEachModuleAndOpensTheOneClicked() {
    Map<String, String> notebooks = new LinkedHashMap<>();
    notebooks.put("Pre-lab 3", "");
    notebooks.put("Lab 4", "");
    NotebookListPanel panel = new NotebookListPanel(notebooks, (title, html) -> true);
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
    List<String> texts = texts(new NotebookListPanel(Map.of(), (title, html) -> true));
    assertTrue(texts.contains("No notebooks yet. Your notebook starts when you begin the lab."));
    assertEquals(2, texts.size(), "just the heading and the message");
  }

  @Test
  void opensWithTheSavedNotesAndSavesOnlyChangedOnesOnLeaving() {
    NotebookPanel saved = new NotebookPanel();
    saved.text.setText("mass 0.52 g");
    List<String> saves = new ArrayList<>();
    NotebookListPanel panel = new NotebookListPanel(Map.of("Pre-lab 3", saved.getHtml()),
        (title, html) -> saves.add(title + "=" + html));

    button(panel, "Pre-lab 3").doClick();
    NotebookPanel notebook = find(panel, NotebookPanel.class);
    assertEquals("mass 0.52 g", notebook.text.getText());
    button(panel, "← All notebooks").doClick();
    assertEquals(List.of(), saves, "unchanged, so nothing to save");

    button(panel, "Pre-lab 3").doClick();
    find(panel, NotebookPanel.class).text.setText("mass 0.61 g");
    // Closing the popup with the notebook still open.
    panel.leaveNotebook();
    assertEquals(1, saves.size());
    assertTrue(saves.get(0).startsWith("Pre-lab 3=") && saves.get(0).contains("0.61"));

    button(panel, "← All notebooks").doClick();
    button(panel, "Pre-lab 3").doClick();
    assertEquals("mass 0.61 g", find(panel, NotebookPanel.class).text.getText(),
        "reopening shows the edit");
  }

  @Test
  void failedSaveKeepsTheNotebookOpenAndTriesAgain() {
    List<String> saves = new ArrayList<>();
    boolean[] works = {false};
    NotebookListPanel panel = new NotebookListPanel(Map.of("Pre-lab 3", ""), (title, html) -> {
      saves.add(html);
      return works[0];
    });
    button(panel, "Pre-lab 3").doClick();
    find(panel, NotebookPanel.class).text.setText("mass 0.61 g");

    button(panel, "← All notebooks").doClick();
    assertNotNull(find(panel, NotebookPanel.class), "still open, with the unsaved notes");
    assertEquals(1, saves.size());

    works[0] = true;
    button(panel, "← All notebooks").doClick();
    assertNull(find(panel, NotebookPanel.class));
    assertEquals(2, saves.size(), "saved on the second try");
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
