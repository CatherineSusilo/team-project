package usecase.guessunknown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.Bench;
import entity.Lab;
import entity.Range;
import entity.StepStatus;
import entity.UnknownSample;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.LabDataAccessInterface;

/**
 * Tests the guess at the end of B7 on a lab whose unknown is aspirin and whose sample melted
 * low (125-132 °C, still a little impure).
 */
class GuessUnknownInteractorTest {
  private final FakeLab bench = new FakeLab();
  private GuessUnknownOutputData output;
  private String error;
  private final GuessUnknownInteractor interactor = new GuessUnknownInteractor(bench,
      new GuessUnknownOutputBoundary() {
        @Override
        public void prepareSuccessView(GuessUnknownOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  /** A lab on B7 with the melting range already measured. */
  private static class FakeLab implements LabDataAccessInterface {
    private final Lab lab = new Lab(List.of(Lab3Definition.steps().get(15)), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    private int snapshots;
    private int saves;
    private boolean diskFull;                              // saving to the file fails

    FakeLab() {
      lab.getRecord().setMeltingRange(new Range(125, 132));
    }

    @Override
    public Lab getLab() {
      return lab;
    }

    @Override
    public void saveLab(Lab saved) {
      if (diskFull) {
        throw new UncheckedIOException(new IOException("disk full"));
      }
      saves++;
    }

    @Override
    public void pushSnapshot() {
      snapshots++;
    }

    @Override
    public boolean popSnapshot() {
      return false;
    }

    @Override
    public void markStepStart(int index) {
    }

    @Override
    public boolean rewindToStepStart(int index) {
      return false;
    }
  }

  @Test
  void rightGuessIsRecordedAndFinishesB7() {
    interactor.execute(new GuessUnknownInputData(ApiType.ASPIRIN));
    assertNull(error);
    assertNotNull(output);
    assertTrue(bench.lab.getRecord().isGuessCorrect());
    assertEquals(StepStatus.COMPLETE, output.getState().getStatus());
    assertEquals(1, bench.snapshots);                      // Undo takes the guess back
    assertEquals(1, bench.saves);
  }

  @Test
  void wrongGuessGivesAHintAndChangesNothing() {
    interactor.execute(new GuessUnknownInputData(ApiType.MEFENAMIC_ACID));
    assertEquals("Your sample melted at 125-132 °C, but mefenamic acid melts at 230-231 °C. "
        + "Try again.", error);
    assertNull(output);
    assertFalse(bench.lab.getRecord().isGuessCorrect());
    assertEquals(0, bench.snapshots);                      // not a mistake to undo
    assertEquals(0, bench.saves);
  }

  @Test
  void wrongGuessCanBeRetried() {
    interactor.execute(new GuessUnknownInputData(ApiType.MEFENAMIC_ACID));
    interactor.execute(new GuessUnknownInputData(ApiType.ASPIRIN));
    assertTrue(bench.lab.getRecord().isGuessCorrect());
  }

  @Test
  void guessNeedsTheMeltingPointFirst() {
    bench.lab.getRecord().setMeltingRange(null);
    interactor.execute(new GuessUnknownInputData(ApiType.ASPIRIN));
    assertEquals("Measure the melting point first.", error);
    assertFalse(bench.lab.getRecord().isGuessCorrect());
    assertEquals(0, bench.snapshots);
  }

  @Test
  void rightGuessStillShowsWhenSavingFails() {
    bench.diskFull = true;
    interactor.execute(new GuessUnknownInputData(ApiType.ASPIRIN));
    assertNotNull(output);                                 // the screen already shows it
    assertEquals("Done, but your progress couldn't be saved.", error);
  }
}
