package usecase.answercheckpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AnswerCheckpointInteractorTest {
  private AnswerCheckpointOutputData output;
  private String error;
  private final AnswerCheckpointInteractor interactor = new AnswerCheckpointInteractor(
      new InMemoryLabDataAccessObject(), new AnswerCheckpointOutputBoundary() {
        @Override
        public void prepareSuccessView(AnswerCheckpointOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void correctAnswerRevealsExplanationAndTakeaway() {
    // Step 0's right answer is the first choice.
    interactor.execute(new AnswerCheckpointInputData(0, Set.of(0)));
    assertTrue(output.isCorrect());
    assertFalse(output.getExplanation().isEmpty());
    assertFalse(output.getTakeaway().isEmpty());
  }

  @Test
  void wrongAnswerHidesTheSolution() {
    interactor.execute(new AnswerCheckpointInputData(0, Set.of(1)));
    assertFalse(output.isCorrect());
    assertEquals("", output.getExplanation());
    assertEquals("", output.getTakeaway());
  }

  @Test
  void tickAllNeedsEveryCorrectChoiceAndNothingElse() {
    // Step 3 asks to tick all: choices 0 and 1 are right.
    interactor.execute(new AnswerCheckpointInputData(3, Set.of(0)));
    assertFalse(output.isCorrect());
    interactor.execute(new AnswerCheckpointInputData(3, Set.of(0, 1, 2)));
    assertFalse(output.isCorrect());
    interactor.execute(new AnswerCheckpointInputData(3, Set.of(0, 1)));
    assertTrue(output.isCorrect());
  }

  @Test
  void emptyAnswerFails() {
    interactor.execute(new AnswerCheckpointInputData(0, Set.of()));
    assertEquals("Pick an answer first.", error);
    assertNull(output);
  }

  @Test
  void unknownStepFails() {
    interactor.execute(new AnswerCheckpointInputData(99, Set.of(0)));
    assertEquals("There is no checkpoint after step 99.", error);
  }
}
