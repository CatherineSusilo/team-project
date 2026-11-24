package usecase.loaddashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import data.RandomChoiceOrderer;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import usecase.ChoiceOrderer;
import usecase.loaddashboard.LoadDashboardOutputData.ModuleInfo;
import usecase.loaddashboard.LoadDashboardOutputData.QuestionInfo;

class LoadDashboardInteractorTest {

  @Test
  void questionTypesBecomeFlags() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of("goal"),
        List.of("Part A"), null, List.of(
            new QuizQuestion("single", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
            new QuizQuestion("multiple", Type.MULTIPLE, List.of("a", "b"), Set.of(0, 1), ""),
            new QuizQuestion("image", Type.IMAGE, List.of("/a.png", "/b.png"), Set.of(0), ""),
            new QuizQuestion("equation", "A + B", List.of("C", "D", "E"), Set.of(0, 2), "")));
    ModuleInfo info = load(module);
    assertEquals(List.of("goal"), info.getLearningObjectives());
    assertEquals(List.of("Part A"), info.getPartTitles());
    assertEquals(List.of(0), info.getPartProgress());
    assertEquals(0, info.getProgress());
    assertEquals(List.of(), info.getSavedAnswers());
    List<QuestionInfo> questions = info.getQuestions();
    assertEquals(4, questions.size());
    assertFalse(questions.get(0).isMultipleChoice());
    assertFalse(questions.get(0).isImageChoices());
    assertTrue(questions.get(1).isMultipleChoice());
    assertFalse(questions.get(1).isImageChoices());
    assertFalse(questions.get(2).isMultipleChoice());
    assertTrue(questions.get(2).isImageChoices());
    assertFalse(questions.get(2).isEquation());
    assertEquals("", questions.get(2).getReactants());
    assertEquals(0, questions.get(2).getBlankCount());
    QuestionInfo equation = questions.get(3);
    assertTrue(equation.isEquation());
    assertFalse(equation.isMultipleChoice());
    assertFalse(equation.isImageChoices());
    assertEquals("A + B", equation.getReactants());
    assertEquals(2, equation.getBlankCount());
    assertEquals(List.of("C", "D", "E"), equation.getChoices());
  }

  @Test
  void carriesTheNotebook() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of());
    assertEquals("", load(module).getNotebook());
    module.setNotebook("<b>notes</b>");
    assertEquals("<b>notes</b>", load(module).getNotebook());
  }

  @Test
  void choicesComeBackShuffledAndTheOrderMapsThemToTheOriginals() {
    List<String> original = List.of("a", "b", "c", "d");
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of(new QuizQuestion("q", Type.SINGLE, original, Set.of(0), "")));
    boolean shuffled = false;
    ChoiceOrderer orderer = new RandomChoiceOrderer(new Random(3));
    for (int i = 0; i < 20; i++) {
      QuestionInfo question = load(module, orderer).getQuestions().get(0);
      assertEquals(new HashSet<>(List.of(0, 1, 2, 3)), new HashSet<>(question.getChoiceOrder()));
      for (int shown = 0; shown < 4; shown++) {
        assertEquals(original.get(question.getChoiceOrder().get(shown)),
            question.getChoices().get(shown));
      }
      shuffled |= !question.getChoiceOrder().equals(List.of(0, 1, 2, 3));
    }
    assertTrue(shuffled, "the order changes between loads");
  }

  @Test
  void savedCorrectAndSavedAnswersStayInOriginalIndexesWhenShuffled() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of(
            new QuizQuestion("q", Type.SINGLE, List.of("a", "b", "c", "d"), Set.of(2), "")));
    module.setSavedAnswers(List.of(Set.of(2)));
    ModuleInfo info = load(module, new RandomChoiceOrderer(new Random(5)));
    assertEquals(List.of(Set.of(2)), info.getSavedAnswers());
    assertTrue(info.getQuestions().get(0).isSavedCorrect());
  }

  @Test
  void savedCorrectMarksRightSavedAnswers() {
    LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(),
        List.of(), null, List.of(
            new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
            new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
            new QuizQuestion("q3", Type.SINGLE, List.of("a", "b"), Set.of(0), "")));
    assertFalse(load(module).getQuestions().get(0).isSavedCorrect(), "nothing saved yet");
    module.setSavedAnswers(List.of(Set.of(0), Set.of(1), Set.of()));
    List<QuestionInfo> questions = load(module).getQuestions();
    assertTrue(questions.get(0).isSavedCorrect());
    assertFalse(questions.get(1).isSavedCorrect(), "wrong");
    assertFalse(questions.get(2).isSavedCorrect(), "blank");
  }

  /** Runs LoadDashboard with {@code module} as the only module. */
  private static ModuleInfo load(LabModule module) {
    return load(module, ChoiceOrderer.IN_ORDER);
  }

  /** Runs LoadDashboard with {@code module} as the only module and a chosen shuffle. */
  private static ModuleInfo load(LabModule module, ChoiceOrderer orderer) {
    LoadDashboardOutputData[] out = new LoadDashboardOutputData[1];
    new LoadDashboardInteractor(new InMemoryLabDataAccessObject(List.of(module)),
        new LoadDashboardOutputBoundary() {
          @Override
          public void prepareSuccessView(LoadDashboardOutputData data) {
            out[0] = data;
          }

          @Override
          public void prepareFailView(String error) {
            throw new AssertionError(error);
          }
        }, orderer).execute(new LoadDashboardInputData());
    return out[0].getModules().get(0);
  }
}
