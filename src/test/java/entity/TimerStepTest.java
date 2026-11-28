package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the settling wait (A8, A9): the timer bar shows while the mixed tube is cloudy.
 */
class TimerStepTest {
  private final TimerStep step = new TimerStep(ItemType.CONICAL_TUBE, 1);
  private final Lab lab = new Lab(List.of(step), new Bench(),
      new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel conical =
      new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);

  TimerStepTest() {
    lab.getBench().addItem(conical, new Vec2(0, 0));
    conical.getMixture().add(Substances.WATER, 2);
    conical.getMixture().add(Substances.ETHER, 4);
  }

  @Test
  void aJustMixedTubeAsksForAWait() {
    conical.getMixture().mix(Mixture.FULL_MIX);           // A7: cloudy
    assertEquals(1, step.waitMinutes(lab));               // the timer bar shows 1 minute
    assertFalse(step.isComplete(lab));
  }

  @Test
  void onceSettledThereIsNothingToWaitFor() {
    conical.getMixture().mix(Mixture.FULL_MIX);
    conical.getMixture().advance(1);                      // the timer finished
    assertEquals(0, step.waitMinutes(lab));
    assertTrue(step.isComplete(lab));
  }

  @Test
  void noTubeMeansNoWait() {
    Lab empty = new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    assertEquals(0, step.waitMinutes(empty));
    assertFalse(step.isComplete(empty));
  }

  @Test
  void waitingIsNotAnAction() {
    assertFalse(step.expects(new Action(ActionKind.MOVE_LIQUID, "conical", "pipette", 2, null,
        "")));
  }
}
