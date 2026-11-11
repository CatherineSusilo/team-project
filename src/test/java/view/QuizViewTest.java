package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.CheckQuizController;
import adapter.controller.SaveQuizController;
import adapter.controller.SubmitQuizController;
import adapter.presenter.CheckQuizPresenter;
import adapter.presenter.SaveQuizPresenter;
import adapter.presenter.SubmitQuizPresenter;
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
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import usecase.checkquiz.CheckQuizInteractor;
import usecase.savequiz.SaveQuizInteractor;
import usecase.submitquiz.SubmitQuizInteractor;

class QuizViewTest {
  private static final List<QuizQuestion> QUESTIONS = List.of(
      new QuizQuestion("q1", Type.SINGLE, List.of("right", "wrong"), Set.of(0), ""),
      new QuizQuestion("q2", Type.MULTIPLE, List.of("yes", "no", "also yes"), Set.of(0, 2), ""));

  @Test
  void continueOnlyAppearsWhenEveryAnswerIsCorrect() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, QUESTIONS);
    QuizView quiz = quizFor(module);
    boolean[] submitted = {false};
    quiz.open(new ModuleState("Pre-lab", "", "", false, "", List.of(), List.of(),
        List.of(), List.of(
        new QuestionState("q1", false, false, false, "", 0, List.of("right", "wrong")),
        new QuestionState("q2", true, false, false, "", 0, List.of("yes", "no", "also yes")))),
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

    assertTrue(labelTexts(quiz).stream().anyMatch(text -> text.contains("Correct.")));
    button(quiz, "also yes").setSelected(false);
    assertFalse(continueButton.isVisible(), "hidden again once an answer changes");
    assertEquals(1, labelTexts(quiz).stream().filter(text -> text.contains("Correct.")).count(),
        "only the unchanged question still says Correct");
    button(quiz, "also yes").setSelected(true);
    button(quiz, "Check Answers").doClick();

    continueButton.doClick();
    assertTrue(submitted[0]);
    assertTrue(module.getLastEdited().isPresent(), "Continue stamps the last-edited time");
    assertEquals(100, module.getQuizProgress(), "Continue saves the answers");
  }

  @Test
  void equationTilesFillAndClearTheBlanks() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of(new QuizQuestion("q", "A + B", List.of("C", "D", "E"), Set.of(0, 1), "")));
    QuizView quiz = quizFor(module);
    quiz.open(new ModuleState("Pre-lab", "", "", false, "", List.of(), List.of(),
        List.of(), List.of(
        new QuestionState("q", false, false, true, "A + B", 2, List.of("C", "D", "E")))),
        () -> { }, () -> { });
    AbstractButton tileE = button(quiz, "E");
    tileE.doClick();
    assertFalse(tileE.isEnabled(), "a used tile can't fill a second blank");
    button(quiz, "D").doClick();
    button(quiz, "Check Answers").doClick();
    assertFalse(button(quiz, "Continue").isVisible(), "E is wrong");

    // The first button labelled E is the blank it filled, not the tile.
    button(quiz, "E").doClick();
    assertTrue(tileE.isEnabled(), "clearing a blank frees its tile");
    button(quiz, "C").doClick();
    button(quiz, "Check Answers").doClick();
    assertTrue(button(quiz, "Continue").isVisible(), "C and D in either order is correct");
  }

  @Test
  void backSavesUnfinishedAnswersThenLeaves() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, QUESTIONS);
    QuizView quiz = quizFor(module);
    boolean[] left = {false};
    quiz.open(new ModuleState("Pre-lab", "", "", false, "", List.of(), List.of(),
        List.of(), List.of(
        new QuestionState("q1", false, false, false, "", 0, List.of("right", "wrong")),
        new QuestionState("q2", true, false, false, "", 0, List.of("yes", "no", "also yes")))),
        () -> left[0] = true, () -> { });

    button(quiz, "right").setSelected(true);
    button(quiz, "no").setSelected(true);
    button(quiz, "Back to Dashboard").doClick();
    assertTrue(left[0]);
    assertEquals(List.of(Set.of(0), Set.of(1)), module.getSavedAnswers(),
        "wrong answers are saved too");
    assertEquals(50, module.getQuizProgress());
  }

  /** A quiz page backed by the real use cases, with {@code module} as the only module. */
  private static QuizView quizFor(LabModule module) {
    InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject(List.of(module));
    QuizViewModel viewModel = new QuizViewModel();
    return new QuizView(viewModel,
        new CheckQuizController(
            new CheckQuizInteractor(dataAccess, new CheckQuizPresenter(viewModel))),
        new SubmitQuizController(
            new SubmitQuizInteractor(dataAccess, dataAccess, new SubmitQuizPresenter(viewModel))),
        new SaveQuizController(
            new SaveQuizInteractor(dataAccess, new SaveQuizPresenter(viewModel))));
  }

  private static AbstractButton button(Container container, String text) {
    for (AbstractButton button : buttons(container)) {
      if (text.equals(button.getText())) {
        return button;
      }
    }
    throw new AssertionError("No button labelled " + text);
  }

  private static List<String> labelTexts(Container container) {
    List<String> texts = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (child instanceof JLabel) {
        texts.add(((JLabel) child).getText());
      }
      if (child instanceof Container) {
        texts.addAll(labelTexts((Container) child));
      }
    }
    return texts;
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
