package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderRuleTest {

  private static Lab labExpecting(ActionKind kind) {
    CheckStep step = new CheckStep(0, "A6", "Add NaOH", "Add the NaOH first.", List.of(), false,
        lab -> false, action -> action.getKind() == kind);
    return new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  }

  @Test
  void anActionTheStepExpectsIsFine() {
    Lab lab = labExpecting(ActionKind.ADD);
    Action add = new Action(ActionKind.ADD, "naoh_bottle-1", "conical_tube-1", 0.5, null, "");

    assertNull(new OrderRule().check(lab, add));
  }

  @Test
  void anActionTheStepDoesNotExpectIsFlaggedWithTheStepsHint() {
    Lab lab = labExpecting(ActionKind.ADD);
    Action mix = new Action(ActionKind.MIX, "pipette-1", "conical_tube-1", 30, null, "");

    assertEquals("Add the NaOH first.", new OrderRule().check(lab, mix));
  }
}
