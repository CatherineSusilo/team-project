package usecase.wait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.Bench;
import entity.ItemType;
import entity.Lab;
import entity.Mixture;
import entity.Substances;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.ItemData;
import usecase.LabDataAccessInterface;

/**
 * Tests the Wait use case on a real lab at A8, with the conical tube just mixed (cloudy).
 */
class WaitInteractorTest {
  private final FakeLab bench = new FakeLab();
  private WaitOutputData output;
  private String error;
  private final WaitInteractor interactor = new WaitInteractor(bench,
      new WaitOutputBoundary() {
        @Override
        public void prepareSuccessView(WaitOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  /** A lab on A8 whose bench has the conical tube (just mixed in A7) and the test tube. */
  private static class FakeLab implements LabDataAccessInterface {
    private final Lab lab = new Lab(List.of(Lab3Definition.steps().get(7)), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    private final Vessel conical =
        new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    private int snapshots;
    private int saves;
    private boolean diskFull;                              // saving to the file fails

    FakeLab() {
      lab.getBench().addItem(conical, new Vec2(0, 0));
      lab.getBench().addItem(new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8),
          new Vec2(0, 0));
      conical.getMixture().add(Substances.WATER, 2);
      conical.getMixture().add(Substances.ETHER, 4);
      conical.getMixture().mix(Mixture.FULL_MIX);          // A7: cloudy until it settles
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

  /** How the screen shows one item after the last successful wait. */
  private ItemData shown(String id) {
    for (ItemData item : output.getState().getItems()) {
      if (item.getId().equals(id)) {
        return item;
      }
    }
    return null;
  }

  @Test
  void theTimerBarShowsWhileTheTubeIsCloudy() {
    Lab lab = bench.getLab();
    assertEquals(1, lab.currentStep().waitMinutes(lab));
  }

  @Test
  void waitingSettlesTheTube() {
    interactor.execute(new WaitInputData(1));
    assertNull(error);
    assertTrue(bench.conical.getMixture().isSettled());
    assertTrue(shown("conical").isSettled());              // two clear layers on screen
  }

  @Test
  void afterTheWaitTheTimerBarStops() {
    interactor.execute(new WaitInputData(1));
    assertEquals(0, output.getState().getWaitMinutes());
  }

  @Test
  void aWaitSavesAnUndoPointAndTheLab() {
    interactor.execute(new WaitInputData(1));
    assertEquals(1, bench.snapshots);                       // Undo can go back to cloudy
    assertEquals(1, bench.saves);
  }

  @Test
  void nothingToWaitForFails() {
    bench.conical.getMixture().advance(1);                  // already settled
    interactor.execute(new WaitInputData(1));
    assertEquals("There's nothing to wait for yet.", error);
    assertEquals(0, bench.snapshots);
    assertNull(output);
  }

  @Test
  void zeroMinutesFails() {
    interactor.execute(new WaitInputData(0));
    assertEquals("Choose a time above 0.", error);
    assertFalse(bench.conical.getMixture().isSettled());
  }

  @Test
  void aFailedSaveStillShowsTheWaitWithAWarning() {
    bench.diskFull = true;
    interactor.execute(new WaitInputData(1));
    assertEquals("Done, but your progress couldn't be saved.", error);
    assertNotNull(output);                                  // the settled tube is still shown
  }
}
