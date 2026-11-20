package usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Lab;
import entity.LabStep;
import entity.StepResult;
import entity.StepStatus;
import entity.Substance;
import entity.Substances;
import entity.Transfer;
import entity.WrongTargetRule;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LabStateDataTest {

  /** A step that is finished as soon as it is looked at, and that wants ether in the conical tube. */
  private static final class EtherStep extends LabStep {
    private final StepResult result;

    EtherStep(String title, StepResult result) {
      super(title, title, "Add ether to the " + title);
      this.result = result;
    }

    @Override
    public StepResult evaluate(Lab lab) {
      return result;
    }

    @Override
    public Map<Substance, String> requiredTargets() {
      return Map.of(Substances.ETHER, "conical tube");
    }
  }

  private static Lab lab(StepResult first) {
    return new Lab(
        List.of(new EtherStep("Task A", first), new EtherStep("Task B", StepResult.complete("ok"))),
        List.of(new WrongTargetRule()));
  }

  @Test
  void copiesTheStepAndPosition() {
    Lab lab = lab(StepResult.incomplete("Add 4 mL"));
    LabStateData data = LabStateData.from(lab, lab.evaluateCurrentStep());
    assertEquals(0, data.getStepIndex());
    assertEquals(2, data.getStepCount());
    assertEquals("Task A", data.getTitle());
    assertEquals("Add ether to the Task A", data.getInstruction());
    assertEquals(StepStatus.INCOMPLETE, data.getStatus());
    assertEquals("Add 4 mL", data.getMessage());
  }

  @Test
  void firstStepLocksPreviousOnly() {
    Lab lab = lab(StepResult.complete("done"));
    LabStateData data = LabStateData.from(lab, lab.evaluateCurrentStep());
    assertTrue(data.isPrevLocked());
    assertFalse(data.isNextLocked());
  }

  @Test
  void incompleteStepLocksNext() {
    Lab lab = lab(StepResult.incomplete("keep going"));
    assertTrue(LabStateData.from(lab, lab.evaluateCurrentStep()).isNextLocked());
  }

  @Test
  void mistakeFromARulePresentsAsMistakeAndLocksNext() {
    Lab lab = lab(StepResult.complete("done"));
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.ETHER, 4));
    LabStateData data = LabStateData.from(lab, lab.evaluateCurrentStep());
    assertEquals(StepStatus.MISTAKE, data.getStatus());
    assertEquals("Diethyl ether goes in the conical tube.", data.getMessage());
    assertTrue(data.isNextLocked());
  }

  @Test
  void mistakeFromAStepItselfAlsoLocksNext() {
    Lab lab = lab(StepResult.mistake("Too much: 0.215 g. Undo or remove some."));
    LabStateData data = LabStateData.from(lab, lab.evaluateCurrentStep());
    assertEquals(StepStatus.MISTAKE, data.getStatus());
    assertTrue(data.isNextLocked());
  }
}
