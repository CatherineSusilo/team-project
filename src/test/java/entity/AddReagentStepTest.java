package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests adding drops of a reagent (B1: 20 drops of HCl into the test tube).
 */
class AddReagentStepTest {
  private final AddReagentStep step = new AddReagentStep(ItemType.TEST_TUBE, Substances.HCL, 20);
  private final Lab lab = new Lab(List.of(step), new Bench(),
      new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel testTube =
      new Vessel("test_tube-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);

  AddReagentStepTest() {
    lab.getBench().addItem(testTube, new Vec2(0, 0));
    testTube.getMixture().add(Substances.WATER, 3);
  }

  /** One drop at a time, like clicking the dropper. */
  private void drops(int count) {
    for (int i = 0; i < count; i++) {
      testTube.getMixture().add(Substances.HCL, StockBottle.DROP_ML);
    }
  }

  @Test
  void noDropsIsIncomplete() {
    assertFalse(step.isComplete(lab));
  }

  @Test
  void nineteenDropsIsIncomplete() {
    drops(19);
    assertFalse(step.isComplete(lab));
  }

  @Test
  void twentyDropsIsComplete() {
    drops(20);
    assertTrue(step.isComplete(lab));
  }

  @Test
  void theRangeIsExactlyTwentyDropsOfHcl() {
    assertEquals(1.0, step.amountRange().getLo(), 1e-9);
    assertEquals(1.0, step.amountRange().getHi(), 1e-9);  // AmountRule flags drop 21
    assertEquals(Substances.HCL, step.amountSubstance());
  }

  @Test
  void addingBelongsToTheStep() {
    assertTrue(step.expects(new Action(ActionKind.ADD, "hcl_bottle-1", "test_tube-1", 1, null,
        "")));
    assertFalse(step.expects(new Action(ActionKind.MIX, "pipette-1", "test_tube-1", 5, null,
        "")));
  }
}
