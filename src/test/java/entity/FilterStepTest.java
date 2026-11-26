package entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests B3's FilterStep: done once the solid is on the funnel and it has been rinsed twice.
 */
class FilterStepTest {
  private final FilterStep step = new FilterStep(11, "B3 Collect the solid", "Filter it.",
      "Tip it onto the funnel.", List.of("pipette"), 2);
  private final Lab lab = new Lab(List.of(step), new Bench(),
      new UnknownSample('A', ApiType.ASPIRIN));
  private final BuchnerFunnel funnel = new BuchnerFunnel("funnel", new Vec2(0, 0), 10, 60);

  FilterStepTest() {
    lab.getBench().addItem(funnel, new Vec2(0, 0));
  }

  @Test
  void nothingOnTheFunnelIsIncomplete() {
    assertFalse(step.isComplete(lab));
  }

  @Test
  void solidWithoutRinsingIsIncomplete() {
    funnel.getMixture().add(Substances.ASPIRIN, 0.150);
    assertFalse(step.isComplete(lab));
  }

  @Test
  void oneRinseIsIncomplete() {
    funnel.getMixture().add(Substances.ASPIRIN, 0.150);
    funnel.addRinse();
    assertFalse(step.isComplete(lab));
  }

  @Test
  void solidAndTwoRinsesAreComplete() {
    funnel.getMixture().add(Substances.ASPIRIN, 0.150);
    funnel.addRinse();
    funnel.addRinse();
    assertTrue(step.isComplete(lab));
  }

  @Test
  void withoutAFunnelOnTheBenchItIsIncomplete() {
    Lab noFunnel = new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    assertFalse(step.isComplete(noFunnel));
  }

  @Test
  void pouringBelongsToTheStep() {
    assertTrue(step.expects(new Action(ActionKind.MOVE_LIQUID, "testTube", "funnel", 3, null,
        "")));
    assertFalse(step.expects(new Action(ActionKind.MIX, "pipette", "testTube", 15, null, "")));
  }
}
