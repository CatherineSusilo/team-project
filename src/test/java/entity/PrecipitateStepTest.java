package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests B2's 10 minutes in the ice bath: the timer bar shows while the test tube is in it.
 */
class PrecipitateStepTest {
  private final PrecipitateStep step = new PrecipitateStep(ItemType.TEST_TUBE, 10);
  private final Lab lab = new Lab(List.of(step), new Bench(),
      new UnknownSample('A', ApiType.ASPIRIN));
  private final IceBath bath = new IceBath("ice_bath-1", new Vec2(0, 0));
  private final Vessel testTube =
      new Vessel("test_tube-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);

  PrecipitateStepTest() {
    lab.getBench().addItem(bath, new Vec2(0, 0));
    lab.getBench().addItem(testTube, new Vec2(0, 0));
  }

  @Test
  void outOfTheBathThereIsNoWait() {
    assertEquals(0, step.waitMinutes(lab));
    assertFalse(step.isComplete(lab));
  }

  @Test
  void inTheBathTheTimerBarShowsTenMinutes() {
    bath.immerse(testTube);
    assertEquals(10, step.waitMinutes(lab));
    assertFalse(step.isComplete(lab));
  }

  @Test
  void afterTenMinutesItIsDone() {
    bath.immerse(testTube);
    lab.advanceTime(10);                                  // the Wait use case
    assertTrue(step.isComplete(lab));
    assertEquals(0, step.waitMinutes(lab));
  }

  @Test
  void takenOutEarlyTheTimerStops() {
    bath.immerse(testTube);
    bath.release("test_tube-1");                          // lifted out before the timer ends
    assertEquals(0, step.waitMinutes(lab));
    assertFalse(step.isComplete(lab));
  }

  @Test
  void stillDoneAfterItIsTakenOut() {
    bath.immerse(testTube);
    lab.advanceTime(10);
    bath.release("test_tube-1");                          // lifted out after the wait
    assertTrue(step.isComplete(lab));
  }

  @Test
  void noIceBathMeansNoWait() {
    Lab noBath = new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    noBath.getBench().addItem(testTube, new Vec2(0, 0));
    assertEquals(0, step.waitMinutes(noBath));
    assertFalse(step.isComplete(noBath));
  }

  @Test
  void movingTheTubeBelongsToTheStep() {
    assertTrue(step.expects(new Action(ActionKind.MOVE, "test_tube-1", "ice_bath-1", 0, null,
        "")));
    assertFalse(step.expects(new Action(ActionKind.MIX, "pipette-1", "test_tube-1", 5, null,
        "")));
  }
}
