package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;

class QuizViewTest {

  @Test
  void continueOnlyAppearsWhenEveryAnswerIsCorrect() {
    boolean[] continued = {false};
    QuizView quiz = new QuizView("Pre-lab", List.of(
        new QuizQuestion("q1", Type.SINGLE, List.of("right", "wrong"), Set.of(0), ""),
        new QuizQuestion("q2", Type.MULTIPLE, List.of("yes", "no", "also yes"), Set.of(0, 2),
            "")),
        () -> { }, () -> continued[0] = true);
    AbstractButton continueButton = button(quiz, "Continue");
    assertFalse(continueButton.isVisible(), "hidden before checking");

    button(quiz, "right").setSelected(true);
    button(quiz, "yes").setSelected(true);
    button(quiz, "Check Answers").doClick();
    assertFalse(continueButton.isVisible(), "hidden while q2 is missing a choice");

    button(quiz, "also yes").setSelected(true);
    button(quiz, "Check Answers").doClick();
    assertTrue(continueButton.isVisible(), "shown once everything is correct");

    continueButton.doClick();
    assertTrue(continued[0]);
  }

  private static AbstractButton button(Container container, String text) {
    for (AbstractButton button : buttons(container)) {
      if (text.equals(button.getText())) {
        return button;
      }
    }
    throw new AssertionError("No button labelled " + text);
  }

  private static List<AbstractButton> buttons(Container container) {
    List<AbstractButton> found = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (child instanceof AbstractButton) {
        found.add((AbstractButton) child);
      }
      if (child instanceof Container) {
        found.addAll(buttons((Container) child));
      }
    }
    return found;
  }
}
