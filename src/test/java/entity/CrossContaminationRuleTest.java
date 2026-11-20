package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;

class CrossContaminationRuleTest {
  private final CrossContaminationRule rule = new CrossContaminationRule();
  private final Lab lab = new Lab(List.of(new StubStep("Task A", Substances.WATER)), List.of());

  @Test
  void waterPipetteDippedInEtherIsMistake() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    lab.recordTransfer(new Transfer("pipette-1", "conical tube", Substances.ETHER, 4));
    assertEquals("Use one pipette per solvent.", rule.check(lab).get());
  }

  @Test
  void etherPipetteDippedInWaterIsMistake() {
    lab.recordTransfer(new Transfer("pipette-2", "conical tube", Substances.ETHER, 4));
    lab.recordTransfer(new Transfer("pipette-2", "test tube", Substances.WATER, 2));
    assertEquals("Use one pipette per solvent.", rule.check(lab).get());
  }

  @Test
  void sameSolventTwiceIsFine() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void differentPipettesAreFine() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    lab.recordTransfer(new Transfer("pipette-2", "conical tube", Substances.ETHER, 4));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void reagentsAreNotSolvents() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.NAOH, 1));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void noTransfersIsFine() {
    assertFalse(rule.check(lab).isPresent());
  }
}
