package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class CrossContaminationRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A5", "Mix", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));

  CrossContaminationRuleTest() {
    lab.getBench().addItem(pipette, new Vec2(1, 1));
    lab.getBench().addItem(new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6),
        new Vec2(1, 1));
  }

  private String move(String sourceId, String targetId) {
    return new CrossContaminationRule().check(lab,
        new Action(ActionKind.MOVE_LIQUID, sourceId, targetId, 1, null, ""));
  }

  @Test
  void aPipetteWithOneSolventIsFine() {
    pipette.getMixture().add(Substances.WATER, 1);

    assertNull(move("pipette-1", "conical-1"));
  }

  @Test
  void aPipetteThatHeldWaterAndEtherIsFlaggedWhenItIsTheSource() {
    pipette.getMixture().add(Substances.WATER, 1);
    pipette.getMixture().add(Substances.ETHER, 1);

    assertEquals("That pipette has had both water and ether in it. Undo and use a separate "
        + "pipette for each.", move("pipette-1", "conical-1"));
  }

  @Test
  void aPipetteThatHeldWaterAndEtherIsFlaggedWhenItIsTheTarget() {
    pipette.getMixture().add(Substances.WATER, 1);
    pipette.getMixture().add(Substances.ETHER, 1);

    assertEquals("That pipette has had both water and ether in it. Undo and use a separate "
        + "pipette for each.", move("conical-1", "pipette-1"));
  }

  @Test
  void onlyMovingLiquidIsChecked() {
    pipette.getMixture().add(Substances.WATER, 1);
    pipette.getMixture().add(Substances.ETHER, 1);
    Action mix = new Action(ActionKind.MIX, "pipette-1", "conical-1", 15, null, "");

    assertNull(new CrossContaminationRule().check(lab, mix));
  }
}
