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

    assertEquals("Use one pipette per solvent.", move("pipette-1", "conical-1"));
  }

  @Test
  void aPipetteThatHeldWaterAndEtherIsFlaggedWhenItIsTheTarget() {
    pipette.getMixture().add(Substances.WATER, 1);
    pipette.getMixture().add(Substances.ETHER, 1);

    assertEquals("Use one pipette per solvent.", move("conical-1", "pipette-1"));
  }

  @Test
  void otherActionsAreIgnored() {
    pipette.getMixture().add(Substances.WATER, 1);
    pipette.getMixture().add(Substances.ETHER, 1);
    Action use = new Action(ActionKind.USE, "pipette-1", null, 0, Operation.LABEL, "x");

    assertNull(new CrossContaminationRule().check(lab, use));
  }

  @Test
  void fillingAPipetteFromABottleWithTheOtherSolventInItIsFlagged() {
    pipette.getMixture().add(Substances.ETHER, 1);
    pipette.getMixture().add(Substances.WATER, 1);
    Action fill = new Action(ActionKind.ADD, "water_bottle-1", "pipette-1", 1, null, "");

    assertEquals("Use one pipette per solvent.", new CrossContaminationRule().check(lab, fill));
  }

  @Test
  void fillingAPipetteWithTheSameSolventIsFine() {
    pipette.getMixture().add(Substances.WATER, 1);
    Action fill = new Action(ActionKind.ADD, "water_bottle-1", "pipette-1", 1, null, "");

    assertNull(new CrossContaminationRule().check(lab, fill));
  }

  private String mix(String targetId) {
    return new CrossContaminationRule().check(lab,
        new Action(ActionKind.MIX, "pipette-1", targetId, 15, null, ""));
  }

  @Test
  void aNewPipetteThatIsMixingTheTubeIsFine() {
    pipette.markMixing("conical-1");

    assertNull(mix("conical-1"));
  }

  @Test
  void thePipetteThatIsAlreadyMixingTheTubeIsFine() {
    pipette.markMixing("conical-1");
    pipette.markUsed();

    assertNull(mix("conical-1"));
  }

  @Test
  void aPipetteThatHeldASolventBeforeIsFlaggedWhenMixing() {
    pipette.markUsed();
    pipette.markMixing("conical-1");

    assertEquals("Use a new pipette to mix.", mix("conical-1"));
  }

  @Test
  void aUsedPipetteMixingAnotherTubeIsFlagged() {
    pipette.markMixing("conical-1");
    pipette.markUsed();

    assertEquals("Use a new pipette to mix.", mix("conical-2"));
  }

  @Test
  void aMixWithNoTargetIsIgnored() {
    pipette.markUsed();

    assertNull(mix(null));
  }

  @Test
  void aMixWithAMissingPipetteIsIgnored() {
    Action mix = new Action(ActionKind.MIX, "missing-1", "conical-1", 15, null, "");

    assertNull(new CrossContaminationRule().check(lab, mix));
  }
}
