package usecase.checkquiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import usecase.checkquiz.CheckQuizOutputData.QuestionResult;

class CheckQuizInteractorTest {
  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      false, null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), "because a"),
          new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), "because b")));
  private final InMemoryLabDataAccessObject dataAccess =
      new InMemoryLabDataAccessObject(List.of(module));
  private CheckQuizOutputData output;
  private String error;
  private final CheckQuizInteractor interactor = new CheckQuizInteractor(dataAccess, dataAccess,
      new CheckQuizOutputBoundary() {
        @Override
        public void prepareSuccessView(CheckQuizOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void checkReportsEachQuestionWithoutSaving() {
    interactor.execute(new CheckQuizInputData("Pre-lab", List.of(Set.of(0), Set.of()), false));
    QuestionResult first = output.getResults().get(0);
    QuestionResult second = output.getResults().get(1);
    assertTrue(first.isAnswered() && first.isCorrect());
    assertEquals("because a", first.getExplanation());
    assertFalse(second.isAnswered() || second.isCorrect());
    assertFalse(output.isAllCorrect());
    assertNull(module.getLastEdited().orElse(null));
  }

  @Test
  void submitWithWrongAnswersFailsAndSavesNothing() {
    interactor.execute(new CheckQuizInputData("Pre-lab", List.of(Set.of(0), Set.of(0)), true));
    assertEquals("Answer every question correctly before continuing.", error);
    assertNull(output);
    assertNull(module.getLastEdited().orElse(null));
  }

  @Test
  void submitWithCorrectAnswersSetsLastModified() {
    interactor.execute(new CheckQuizInputData("Pre-lab", List.of(Set.of(0), Set.of(1)), true));
    assertTrue(output.isSubmitted());
    assertTrue(module.getLastEdited().isPresent());
  }

  @Test
  void unknownModuleFails() {
    interactor.execute(new CheckQuizInputData("Nope", List.of(), false));
    assertEquals("There is no module called Nope.", error);
  }
}
