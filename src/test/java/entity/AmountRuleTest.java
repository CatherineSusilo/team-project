package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class AmountRuleTest {

  /** A step that allows between 5 and 7 mL in its container. */
  private static final class SolventStep extends LabStep {
    SolventStep() {
      super(0, "A3", "Measure about 6 mL", "Measure about 6 mL.", List.of(), false);
    }

    @Override
    public boolean isComplete(Lab lab) {
      return false;
    }

    @Override
    public boolean expects(Action action) {
      return true;
    }

    @Override
    public Range amountRange() {
      return new Range(5, 7);
    }
  }

  private Lab lab;

  private void fill(double ml) {
    lab = new Lab(List.of(new SolventStep()), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    Vessel cylinder = new Vessel("cylinder-1", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0),
        10, 25);
    cylinder.getMixture().add(Substances.WATER, ml);
    lab.getBench().addItem(cylinder, new Vec2(5, 5));
  }

  private static Action addTo(ActionKind kind) {
    return new Action(kind, "water_bottle-1", "cylinder-1", 1, null, "");
  }

  @Test
  void anAmountInRangeIsFine() {
    fill(6);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void anAmountUnderTheRangeIsNotAMistake() {
    fill(3);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void exactlyTheMostIsFine() {
    fill(7);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.MOVE_LIQUID)));
  }

  @Test
  void overTheMostIsFlaggedWithBothAmounts() {
    fill(8.5);

    assertEquals("That's too much: 8.5 mL, and the most is 7.0 mL. Undo and add less.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void aStepWithNoAmountRangeIsNeverFlagged() {
    lab = new Lab(List.of(new CheckStep(0, "A1", "Record", "Hint", List.of(), false,
        l -> false, a -> true)), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    Vessel cylinder = new Vessel("cylinder-1", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0),
        10, 25);
    cylinder.getMixture().add(Substances.WATER, 9);
    lab.getBench().addItem(cylinder, new Vec2(5, 5));

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void actionsThatAddNoLiquidAreIgnored() {
    fill(9);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.MIX)));
  }

  @Test
  void aTargetThatIsNotAVesselIsIgnored() {
    fill(9);
    Action toScale = new Action(ActionKind.ADD, "water_bottle-1", "missing-1", 1, null, "");

    assertNull(new AmountRule().check(lab, toScale));
  }
}
