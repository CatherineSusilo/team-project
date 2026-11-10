package usecase.submitquiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class SubmitQuizInteractorTest {
  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), List.of(), null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
          new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), ""),
          new QuizQuestion("q3", Type.SINGLE, List.of("a", "b"), Set.of(1), "")));
  private final InMemoryLabDataAccessObject dataAccess =
      new InMemoryLabDataAccessObject(List.of(module));
  private SubmitQuizOutputData output;
  private String error;
  private final SubmitQuizInteractor interactor = new SubmitQuizInteractor(dataAccess,
      dataAccess, new SubmitQuizOutputBoundary() {
        @Override
        public void prepareSuccessView(SubmitQuizOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void wrongAnswersFailAndSaveNothing() {
    interactor.execute(new SubmitQuizInputData("Pre-lab",
        List.of(Set.of(0), Set.of(0), Set.of(1))));
    assertEquals("Question 2 still needs a correct answer.", error);
    assertNull(output);
    assertNull(module.getLastEdited().orElse(null));
    assertEquals(List.of(), module.getSavedAnswers());
  }

  @Test
  void unansweredQuestionFails() {
    interactor.execute(new SubmitQuizInputData("Pre-lab",
        List.of(Set.of(), Set.of(), Set.of())));
    assertEquals("Questions 1, 2 and 3 still need a correct answer.", error);
    assertNull(module.getLastEdited().orElse(null));
  }

  @Test
  void twoWrongQuestionsAreJoinedWithAnd() {
    interactor.execute(new SubmitQuizInputData("Pre-lab",
        List.of(Set.of(1), Set.of(1), Set.of(0))));
    assertEquals("Questions 1 and 3 still need a correct answer.", error);
  }

  @Test
  void correctAnswersAreSaved() {
    interactor.execute(new SubmitQuizInputData("Pre-lab",
        List.of(Set.of(0), Set.of(1), Set.of(1))));
    assertEquals("Pre-lab", output.getModuleTitle());
    assertTrue(module.getLastEdited().isPresent());
    assertEquals(List.of(Set.of(0), Set.of(1), Set.of(1)), module.getSavedAnswers());
    assertEquals(100, module.getQuizProgress());
  }

  @Test
  void unknownModuleFails() {
    interactor.execute(new SubmitQuizInputData("Nope", List.of()));
    assertEquals("There is no module called Nope.", error);
  }
}
