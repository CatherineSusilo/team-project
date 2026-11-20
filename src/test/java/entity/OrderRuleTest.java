package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrderRuleTest {
  private final LabStep taskA = new StubStep("Task A", Substances.NAOH);
  private final LabStep taskB = new StubStep("Task B", Substances.HCL);
  private final List<LabStep> steps = List.of(taskA, taskB);
  private final OrderRule rule = new OrderRule(steps);
  private final Lab lab = new Lab(steps, List.of(rule));

  @Test
  void hclBeforeNaohIsMistake() {
    lab.recordTransfer(new Transfer("dropper-1", "test tube", Substances.HCL, 1));
    Optional<String> result = rule.check(lab);
    assertEquals("HCl is added in Task B. Undo and add NaOH first.", result.get());
  }

  @Test
  void substanceForThisStepIsFine() {
    lab.recordTransfer(new Transfer("dropper-1", "test tube", Substances.NAOH, 1));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void substanceForAnEarlierStepIsFine() {
    LabStep done = new LabStep("Task A", "Task A", "do Task A") {
      @Override
      public StepResult evaluate(Lab lab) {
        return StepResult.complete("done");
      }

      @Override
      public java.util.Set<Substance> addedSubstances() {
        return java.util.Set.of(Substances.NAOH);
      }
    };
    List<LabStep> two = List.of(done, taskB);
    Lab onSecondStep = new Lab(two, List.of());
    onSecondStep.advance();
    onSecondStep.recordTransfer(new Transfer("dropper-1", "test tube", Substances.NAOH, 1));
    assertFalse(new OrderRule(two).check(onSecondStep).isPresent());
  }

  @Test
  void substanceInNoStepIsFine() {
    lab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void substanceBelongingToThisAndALaterStepIsFine() {
    LabStep both = new StubStep("Task A", Substances.WATER);
    LabStep later = new StubStep("Task B", Substances.WATER);
    List<LabStep> repeated = List.of(both, later);
    Lab repeatedLab = new Lab(repeated, List.of());
    repeatedLab.recordTransfer(new Transfer("pipette-1", "test tube", Substances.WATER, 2));
    assertFalse(new OrderRule(repeated).check(repeatedLab).isPresent());
  }

  @Test
  void noTransfersIsFine() {
    assertFalse(rule.check(lab).isPresent());
  }

  @Test
  void labReportsItAsMistake() {
    lab.recordTransfer(new Transfer("dropper-1", "test tube", Substances.HCL, 1));
    assertEquals(StepStatus.MISTAKE, lab.evaluateCurrentStep().getStatus());
  }
}
