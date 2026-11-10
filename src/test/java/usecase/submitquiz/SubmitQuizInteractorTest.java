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
      List.of(), false, null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
          new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), "")));
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
    interactor.execute(new SubmitQuizInputData("Pre-lab", List.of(Set.of(0), Set.of(0))));
    assertEquals("Answer every question correctly before continuing.", error);
    assertNull(output);
    assertNull(module.getLastEdited().orElse(null));
  }

  @Test
  void unansweredQuestionFails() {
    interactor.execute(new SubmitQuizInputData("Pre-lab", List.of(Set.of(0), Set.of())));
    assertEquals("Answer every question correctly before continuing.", error);
    assertNull(module.getLastEdited().orElse(null));
  }

  @Test
  void correctAnswersSetLastModified() {
    interactor.execute(new SubmitQuizInputData("Pre-lab", List.of(Set.of(0), Set.of(1))));
    assertEquals("Pre-lab", output.getModuleTitle());
    assertTrue(module.getLastEdited().isPresent());
  }

  @Test
  void unknownModuleFails() {
    interactor.execute(new SubmitQuizInputData("Nope", List.of()));
    assertEquals("There is no module called Nope.", error);
  }
}
