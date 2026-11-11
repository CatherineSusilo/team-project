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
import java.util.stream.Collectors;
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
  void nextWalksThroughQuestionsAndSubmitQuizNeedsEveryAnswerRight() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, QUESTIONS);
    QuizView quiz = quizFor(module);
    boolean[] submitted = {false};
    quiz.open(twoQuestions(List.of(), false), () -> { }, () -> submitted[0] = true);
    assertTrue(texts(quiz).contains("Question 1"));
    assertFalse(button(quiz, "← Back").isEnabled(), "no question before the first");

    button(quiz, "right").setSelected(true);
    button(quiz, "Check Answer").doClick();
    assertTrue(texts(quiz).stream().anyMatch(text -> text.contains("Correct.")));
    button(quiz, "Next →").doClick();
    assertTrue(texts(quiz).contains("Question 2"));
    assertFalse(texts(quiz).stream().anyMatch(text -> text.contains("Correct.")),
        "question 2's card shows only its own feedback");

    button(quiz, "yes").setSelected(true);
    button(quiz, "Submit Quiz").doClick();
    assertFalse(submitted[0]);
    assertTrue(texts(quiz).contains("Question 2 still needs a correct answer."));

    button(quiz, "also yes").setSelected(true);
    button(quiz, "Submit Quiz").doClick();
    assertTrue(submitted[0]);
    assertTrue(module.getLastEdited().isPresent(), "Submit Quiz stamps the last-edited time");
    assertEquals(100, module.getQuizProgress(), "Submit Quiz saves the answers");
  }

  @Test
  void equationTilesFillAndClearTheBlanks() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of(new QuizQuestion("q", "A + B", List.of("C", "D", "E"),
            Set.of(0, 1), "")));
    QuizView quiz = quizFor(module);
    quiz.open(equation(List.of()), () -> { }, () -> { });
    AbstractButton tileE = button(quiz, "E");
    tileE.doClick();
    assertFalse(tileE.isEnabled(), "a used tile can't fill a second blank");
    button(quiz, "D").doClick();
    button(quiz, "Check Answer").doClick();
    assertTrue(texts(quiz).stream().anyMatch(text -> text.contains("Not quite.")), "E is wrong");

    // The first button labelled E is the blank it filled, not the tile.
    button(quiz, "E").doClick();
    assertTrue(tileE.isEnabled(), "clearing a blank frees its tile");
    button(quiz, "C").doClick();
    button(quiz, "Check Answer").doClick();
    assertTrue(texts(quiz).stream().anyMatch(text -> text.contains("Correct.")),
        "C and D in either order is correct");
  }

  @Test
  void exitModuleSavesUnfinishedAnswersThenLeaves() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, QUESTIONS);
    QuizView quiz = quizFor(module);
    boolean[] left = {false};
    quiz.open(twoQuestions(List.of(), false), () -> left[0] = true, () -> { });

    button(quiz, "right").setSelected(true);
    button(quiz, "Next →").doClick();
    button(quiz, "no").setSelected(true);
    button(quiz, "Exit Module").doClick();
    assertTrue(left[0]);
    assertEquals(List.of(Set.of(0), Set.of(1)), module.getSavedAnswers(),
        "wrong answers are saved too");
    assertEquals(50, module.getQuizProgress());
  }

  @Test
  void reopeningRestoresAnswersAndStartsAtFirstUnfinishedQuestion() {
    QuizView quiz = quizFor(new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
        List.of(), List.of(), null, QUESTIONS));
    quiz.open(twoQuestions(List.of(Set.of(0), Set.of(1)), true), () -> { }, () -> { });
    assertTrue(texts(quiz).contains("Question 2"), "question 1 is already right");
    assertTrue(button(quiz, "no").isSelected());
    button(quiz, "← Back").doClick();
    assertTrue(button(quiz, "right").isSelected());

    quiz.open(equation(List.of(Set.of(2, 0))), () -> { }, () -> { });
    // Buttons in order: the sidebar's "Question 1", the two blanks, then tiles C, D and E.
    List<String> texts = buttons(quiz).stream().map(AbstractButton::getText)
        .collect(Collectors.toList());
    assertEquals(List.of("Question 1", "C", "E", "C", "D", "E"), texts.subList(0, 6),
        "saved tiles fill the blanks");
    assertFalse(buttons(quiz).get(3).isEnabled(), "tile C is in a blank");
    assertTrue(buttons(quiz).get(4).isEnabled(), "tile D is free");
  }

  /** The two {@link #QUESTIONS}; {@code firstCorrect} marks q1's saved answer as right. */
  private static ModuleState twoQuestions(List<Set<Integer>> saved, boolean firstCorrect) {
    return new ModuleState("Pre-lab", "", "", false, "", List.of(), List.of(), saved, List.of(
        new QuestionState("q1", false, false, false, "", 0, List.of("right", "wrong"),
            firstCorrect),
        new QuestionState("q2", true, false, false, "", 0, List.of("yes", "no", "also yes"),
            false)));
  }

  /** One equation question, A + B → ? + ?, with tiles C, D and E. */
  private static ModuleState equation(List<Set<Integer>> saved) {
    return new ModuleState("Pre-lab", "", "", false, "", List.of(), List.of(), saved, List.of(
        new QuestionState("q", false, false, true, "A + B", 2, List.of("C", "D", "E"), false)));
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

  /** Text of every label the student can see: other questions' cards are hidden. */
  private static List<String> texts(Container container) {
    List<String> texts = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (!child.isVisible()) {
        continue;
      }
      if (child instanceof JLabel) {
        texts.add(((JLabel) child).getText());
      }
      if (child instanceof Container) {
        texts.addAll(texts((Container) child));
      }
    }
    return texts;
  }

  /** Every button the student can see, in layout order. */
  private static List<AbstractButton> buttons(Container container) {
    List<AbstractButton> found = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (!child.isVisible()) {
        continue;
      }
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
