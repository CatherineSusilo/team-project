package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WrongTargetRuleTest {
  private final WrongTargetRule rule = new WrongTargetRule();
  private final LabStep step = new StubStep("Task A", Set.of(Substances.ETHER),
      Map.of(Substances.ETHER, "conical tube"));
  private final Lab lab = new Lab(List.of(step), List.of());

  @Test
  void etherIntoAqueousTubeIsMistake() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.ETHER, 4));
    assertEquals("Diethyl ether goes in the conical tube.", rule.check(lab).get());
  }

  @Test
  void rightVesselIsFine() {
    lab.recordTransfer(new Transfer("pipette-1", "conical tube", Substances.ETHER, 4));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void substanceWithoutARestrictionCanGoAnywhere() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void noTransfersIsFine() {
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void labReportsItAsMistake() {
    Lab ruled = new Lab(List.of(step), List.of(rule));
    ruled.recordTransfer(new Transfer("pipette-1", "test tube", Substances.ETHER, 4));
    assertEquals(StepStatus.MISTAKE, ruled.evaluateCurrentStep().getStatus());
  }
}
