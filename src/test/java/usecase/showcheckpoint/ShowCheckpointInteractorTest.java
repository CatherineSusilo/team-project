package usecase.showcheckpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import org.junit.jupiter.api.Test;

class ShowCheckpointInteractorTest {
  private ShowCheckpointOutputData output;
  private String error;
  private final ShowCheckpointInteractor interactor = new ShowCheckpointInteractor(
      new InMemoryLabDataAccessObject(), new ShowCheckpointOutputBoundary() {
        @Override
        public void prepareSuccessView(ShowCheckpointOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void showsTheQuestionForAStep() {
    interactor.execute(new ShowCheckpointInputData(0));
    assertEquals(0, output.getStepIndex());
    assertEquals("Weigh the sample", output.getStepTitle());
    assertEquals(4, output.getChoices().size());
    assertFalse(output.isMultipleChoice());
  }

  @Test
  void tickAllQuestionsAreMarkedMultipleChoice() {
    interactor.execute(new ShowCheckpointInputData(3));
    assertTrue(output.isMultipleChoice());
  }

  @Test
  void unknownStepFails() {
    interactor.execute(new ShowCheckpointInputData(99));
    assertEquals("There is no checkpoint after step 99.", error);
  }
}
