package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class EtherInAqueousRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A8", "Move", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel testTube = new Vessel("test-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);

  EtherInAqueousRuleTest() {
    lab.getBench().addItem(testTube, new Vec2(1, 1));
  }

  private String check(ActionKind kind) {
    return new EtherInAqueousRule().check(lab,
        new Action(kind, "pipette-1", "test-1", 1, null, ""));
  }

  @Test
  void waterInTheAqueousTubeIsFine() {
    testTube.setLabel("aqueous");
    testTube.getMixture().add(Substances.WATER, 2);

    assertNull(check(ActionKind.MOVE_LIQUID));
  }

  @Test
  void etherInTheAqueousTubeIsFlagged() {
    testTube.setLabel("Aqueous");
    testTube.getMixture().add(Substances.WATER, 2);
    testTube.getMixture().add(Substances.ETHER, 1);

    assertEquals("Ether got into the aqueous test tube. Undo and move only the water layer.",
        check(ActionKind.MOVE_LIQUID));
  }

  @Test
  void etherInAnUnlabelledOrOtherlyLabelledTubeIsFine() {
    testTube.getMixture().add(Substances.ETHER, 1);
    assertNull(check(ActionKind.ADD));

    testTube.setLabel("A");
    assertNull(check(ActionKind.ADD));
  }

  @Test
  void actionsThatMoveNoLiquidAreIgnored() {
    testTube.setLabel("aqueous");
    testTube.getMixture().add(Substances.ETHER, 1);

    assertNull(check(ActionKind.MIX));
  }
}
