package entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests moving the water layer (A8, A9): done at 80% of the water in the test tube, no ether.
 */
class SeparateLayersStepTest {
  private final SeparateLayersStep step =
      new SeparateLayersStep(ItemType.CONICAL_TUBE, ItemType.TEST_TUBE, 0.8);
  private final Lab lab = new Lab(List.of(step), new Bench(),
      new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel conical =
      new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
  private final Vessel testTube =
      new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);

  SeparateLayersStepTest() {
    lab.getBench().addItem(conical, new Vec2(0, 0));
    lab.getBench().addItem(testTube, new Vec2(0, 0));
    conical.getMixture().add(Substances.WATER, 2);        // the water layer, after A7
    conical.getMixture().add(Substances.ETHER, 4);
  }

  /** The pipette's trip: draw this much from the bottom of the conical tube, empty it. */
  private void move(double ml) {
    testTube.getMixture().pour(conical.getMixture().drawFromBottom(ml));
  }

  @Test
  void nothingMovedIsIncomplete() {
    assertFalse(step.isComplete(lab));
  }

  @Test
  void lessThan80PercentIsIncomplete() {
    move(1.5);                                            // 75%
    assertFalse(step.isComplete(lab));
  }

  @Test
  void eightyPercentIsComplete() {
    move(1.6);                                            // leaves a little water behind
    assertTrue(step.isComplete(lab));
  }

  @Test
  void allTheWaterIsComplete() {
    move(2);
    assertTrue(step.isComplete(lab));
  }

  @Test
  void takingSomeEtherTooIsIncomplete() {
    move(2);                                              // all the water
    move(0.5);                                            // then a second draw takes ether
    assertFalse(step.isComplete(lab));
  }

  @Test
  void twoTripsWithThePipetteAddUp() {
    move(1);
    move(0.8);                                            // 1.8 of 2 mL = 90%
    assertTrue(step.isComplete(lab));
  }

  @Test
  void movingTheLiquidBelongsToTheStep() {
    assertTrue(step.expects(new Action(ActionKind.MOVE_LIQUID, "conical", "pipette", 2, null,
        "")));
    assertFalse(step.expects(new Action(ActionKind.MIX, "pipette", "conical", 30, null, "")));
  }
}
