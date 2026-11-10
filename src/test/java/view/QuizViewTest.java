package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.CheckQuizController;
import adapter.presenter.CheckQuizPresenter;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.QuestionState;
import adapter.viewmodel.QuizViewModel;
import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.awt.Component;
import java.awt.Container;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import usecase.checkquiz.CheckQuizInteractor;

class QuizViewTest {
  private static final List<QuizQuestion> QUESTIONS = List.of(
      new QuizQuestion("q1", Type.SINGLE, List.of("right", "wrong"), Set.of(0), ""),
      new QuizQuestion("q2", Type.MULTIPLE, List.of("yes", "no", "also yes"), Set.of(0, 2), ""));

  @Test
  void continueOnlyAppearsWhenEveryAnswerIsCorrect() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", false, null,
        QUESTIONS);
    InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject(List.of(module));
    QuizViewModel viewModel = new QuizViewModel();
    QuizView quiz = new QuizView(viewModel, new CheckQuizController(
        new CheckQuizInteractor(dataAccess, dataAccess, new CheckQuizPresenter(viewModel))));
    boolean[] submitted = {false};
    quiz.open(new ModuleState("Pre-lab", "", "", "", false, "", List.of(
        new QuestionState("q1", "SINGLE", List.of("right", "wrong")),
        new QuestionState("q2", "MULTIPLE", List.of("yes", "no", "also yes")))),
        () -> { }, () -> submitted[0] = true);
    AbstractButton continueButton = button(quiz, "Continue");
    assertFalse(continueButton.isVisible(), "hidden before checking");

    button(quiz, "right").setSelected(true);
    button(quiz, "yes").setSelected(true);
    button(quiz, "Check Answers").doClick();
    assertFalse(continueButton.isVisible(), "hidden while q2 is missing a choice");

    button(quiz, "also yes").setSelected(true);
    button(quiz, "Check Answers").doClick();
    assertTrue(continueButton.isVisible(), "shown once everything is correct");

    button(quiz, "also yes").setSelected(false);
    assertFalse(continueButton.isVisible(), "hidden again once an answer changes");
    button(quiz, "also yes").setSelected(true);
    button(quiz, "Check Answers").doClick();

    continueButton.doClick();
    assertTrue(submitted[0]);
    assertTrue(module.getLastEdited().isPresent(), "Continue sets Last Modified");
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
