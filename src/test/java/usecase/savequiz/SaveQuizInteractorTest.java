package usecase.savequiz;

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

class SaveQuizInteractorTest {
  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), List.of(), null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
          new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), "")));
  private final InMemoryLabDataAccessObject dataAccess =
      new InMemoryLabDataAccessObject(List.of(module));
  private SaveQuizOutputData output;
  private String error;
  private final SaveQuizInteractor interactor = new SaveQuizInteractor(dataAccess,
      new SaveQuizOutputBoundary() {
        @Override
        public void prepareSuccessView(SaveQuizOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void savesUnfinishedAndWrongAnswers() {
    interactor.execute(new SaveQuizInputData("Pre-lab", List.of(Set.of(0), Set.of())));
    assertEquals("Pre-lab", output.getModuleTitle());
    assertEquals(List.of(Set.of(0), Set.of()), module.getSavedAnswers());
    assertEquals(50, module.getQuizProgress());
    assertTrue(module.getLastEdited().isPresent());
  }

  @Test
  void wrongAnswerCountFailsAndSavesNothing() {
    interactor.execute(new SaveQuizInputData("Pre-lab", List.of(Set.of(0))));
    assertEquals("Expected 2 answers but got 1.", error);
    assertNull(output);
    assertEquals(List.of(), module.getSavedAnswers());
  }

  @Test
  void unknownModuleFails() {
    interactor.execute(new SaveQuizInputData("Nope", List.of()));
    assertEquals("There is no module called Nope.", error);
  }
}
