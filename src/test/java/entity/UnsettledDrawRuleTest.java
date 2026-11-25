package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class UnsettledDrawRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A8", "Move", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel tube = new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15,
      6);

  UnsettledDrawRuleTest() {
    lab.getBench().addItem(tube, new Vec2(1, 1));
    lab.getBench().addItem(new PasteurPipette("pipette-1", new Vec2(0, 0)), new Vec2(1, 1));
    tube.getMixture().add(Substances.WATER, 2);
  }

  private String draw() {
    return new UnsettledDrawRule().check(lab,
        new Action(ActionKind.MOVE_LIQUID, "conical-1", "pipette-1", 1, null, ""));
  }

  @Test
  void drawingFromASettledTubeIsFine() {
    assertNull(draw());
  }

  @Test
  void drawingRightAfterMixingIsFlagged() {
    tube.getMixture().mix(30);

    assertEquals("Wait for the layers to separate.", draw());
  }

  @Test
  void drawingAfterWaitingForTheLayersIsFine() {
    tube.getMixture().mix(30);
    tube.getMixture().advance(Mixture.SETTLE_MINUTES);

    assertNull(draw());
  }

  @Test
  void onlyDrawingIntoAPipetteIsChecked() {
    tube.getMixture().mix(30);
    Action pour = new Action(ActionKind.MOVE_LIQUID, "pipette-1", "conical-1", 1, null, "");
    Action mix = new Action(ActionKind.MIX, "pipette-1", "conical-1", 30, null, "");

    assertNull(new UnsettledDrawRule().check(lab, pour));
    assertNull(new UnsettledDrawRule().check(lab, mix));
  }
}
