package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;

class FeedbackBannerTest {

  private static <T extends Component> T find(Container root, Class<T> type) {
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

  @Test
  void startsHidden() {
    assertFalse(new FeedbackBanner().isVisible());
  }

  @Test
  void showMistakeDisplaysTheMessageInRed() {
    FeedbackBanner banner = new FeedbackBanner();
    banner.showMistake("Use one pipette per solvent.");
    assertTrue(banner.isVisible());
    JLabel label = find(banner, JLabel.class);
    assertEquals("Use one pipette per solvent.", label.getText());
    assertEquals(Theme.RED, label.getForeground());
  }

  @Test
  void undoButtonIsOfferedWithAMistake() {
    FeedbackBanner banner = new FeedbackBanner();
    banner.showMistake("Nope.");
    JButton undo = find(banner, JButton.class);
    assertEquals("Undo", undo.getText());
    assertTrue(undo.isVisible());
  }

  @Test
  void clickingUndoRunsTheUndoAction() {
    FeedbackBanner banner = new FeedbackBanner();
    int[] clicks = {0};
    banner.setOnUndo(() -> clicks[0]++);
    banner.showMistake("Nope.");
    find(banner, JButton.class).doClick();
    assertEquals(1, clicks[0]);
  }

  @Test
  void clickingUndoWithNoActionWiredDoesNothing() {
    FeedbackBanner banner = new FeedbackBanner();
    banner.showMistake("Nope.");
    find(banner, JButton.class).doClick();
    assertTrue(banner.isVisible());
  }

  @Test
  void clearHidesTheBannerAndForgetsTheMessage() {
    FeedbackBanner banner = new FeedbackBanner();
    banner.showMistake("Nope.");
    banner.clear();
    assertFalse(banner.isVisible());
    assertEquals("", find(banner, JLabel.class).getText());
  }

  @Test
  void aNewMistakeReplacesTheOldMessage() {
    FeedbackBanner banner = new FeedbackBanner();
    banner.showMistake("first");
    banner.showMistake("second");
    assertEquals("second", find(banner, JLabel.class).getText());
  }
}
