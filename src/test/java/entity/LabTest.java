package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LabTest {

  /** A step that always reports the same result. */
  private static final class FixedStep extends LabStep {
    private final StepResult result;

    FixedStep(String id, StepResult result) {
      super(id, id, "do " + id);
      this.result = result;
    }

    @Override
    public StepResult evaluate(Lab lab) {
      return result;
    }
  }

  private static Lab labWith(StepResult first, MistakeRule... rules) {
    List<LabStep> steps = List.of(
        new FixedStep("a", first), new FixedStep("b", StepResult.complete("ok")));
    return new Lab(steps, List.of(rules));
  }

  @Test
  void stepResultFactoriesSetStatusAndMessage() {
    assertEquals(StepStatus.COMPLETE, StepResult.complete("m").getStatus());
    assertEquals(StepStatus.INCOMPLETE, StepResult.incomplete("m").getStatus());
    assertEquals(StepStatus.MISTAKE, StepResult.mistake("m").getStatus());
    assertEquals("m", StepResult.mistake("m").getMessage());
  }

  @Test
  void noRulesUsesTheStepResult() {
    Lab lab = labWith(StepResult.incomplete("keep going"));
    assertEquals(StepStatus.INCOMPLETE, lab.evaluateCurrentStep().getStatus());
  }

  @Test
  void ruleHitIsMistakeEvenWhenStepWouldBeComplete() {
    MistakeRule always = l -> Optional.of("Nope.");
    Lab lab = labWith(StepResult.complete("done"), always);
    StepResult result = lab.evaluateCurrentStep();
    assertEquals(StepStatus.MISTAKE, result.getStatus());
    assertEquals("Nope.", result.getMessage());
  }

  @Test
  void firstRuleHitWins() {
    MistakeRule first = l -> Optional.of("first");
    MistakeRule second = l -> Optional.of("second");
    Lab lab = labWith(StepResult.complete("done"), first, second);
    assertEquals("first", lab.evaluateCurrentStep().getMessage());
  }

  @Test
  void silentRulesFallThroughToTheStep() {
    MistakeRule quiet = l -> Optional.empty();
    Lab lab = labWith(StepResult.complete("done"), quiet);
    assertEquals(StepStatus.COMPLETE, lab.evaluateCurrentStep().getStatus());
  }

  @Test
  void cannotAdvanceUntilStepIsComplete() {
    Lab lab = labWith(StepResult.incomplete("not yet"));
    assertFalse(lab.advance());
    assertEquals(0, lab.getCurrentIndex());
  }

  @Test
  void emptyLabHasNoCurrentStep() {
    Lab lab = new Lab();
    assertThrows(IllegalStateException.class, lab::getCurrentStep);
    assertThrows(IllegalStateException.class, lab::evaluateCurrentStep);
  }

  @Test
  void emptyLabCannotAdvanceOrGoBack() {
    Lab lab = new Lab();
    assertFalse(lab.advance());
    assertFalse(lab.goBack());
  }

  @Test
  void cannotAdvanceWhileMistakeStands() {
    Lab lab = labWith(StepResult.complete("done"), l -> Optional.of("Nope."));
    assertFalse(lab.advance());
  }

  @Test
  void advancesAndGoesBackWhenComplete() {
    Lab lab = labWith(StepResult.complete("done"));
    assertTrue(lab.advance());
    assertEquals(1, lab.getCurrentIndex());
    assertTrue(lab.goBack());
    assertEquals(0, lab.getCurrentIndex());
    assertFalse(lab.goBack());
  }

  @Test
  void transfersAreLoggedInOrder() {
    Lab lab = labWith(StepResult.complete("done"));
    assertFalse(lab.lastTransfer().isPresent());
    Transfer one = new Transfer("pipette-1", "conical tube", Substances.ETHER, 4);
    Transfer two = new Transfer("pipette-1", "conical tube", Substances.WATER, 2);
    lab.recordTransfer(one);
    lab.recordTransfer(two);
    assertEquals(List.of(one, two), lab.getTransfers());
    assertSame(two, lab.lastTransfer().get());
  }
}
