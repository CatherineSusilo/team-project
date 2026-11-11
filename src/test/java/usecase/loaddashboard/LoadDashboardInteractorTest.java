package usecase.loaddashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
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
        }).execute(new LoadDashboardInputData());
    return out[0].getModules().get(0);
  }
}
