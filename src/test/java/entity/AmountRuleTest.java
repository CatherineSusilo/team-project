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

  /** A part that allows up to a most of one substance, for an add into its container. */
  private static final class PartStep extends LabStep {
    private final double most;
    private final Substance substance;

    PartStep(double most, Substance substance) {
      super(0, "part", "Add", "Hint", List.of(), false);
      this.most = most;
      this.substance = substance;
    }

    @Override
    public boolean isComplete(Lab lab) {
      return false;
    }

    @Override
    public boolean expects(Action action) {
      return action.getKind() == ActionKind.ADD;
    }

    @Override
    public Range amountRange() {
      return new Range(0, most);
    }

    @Override
    public Substance amountSubstance() {
      return substance;
    }
  }

  private Vessel labWith(LabStep step, double waterMl) {
    lab = new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    Vessel tube = new Vessel("cylinder-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    tube.getMixture().add(Substances.WATER, waterMl);
    lab.getBench().addItem(tube, new Vec2(5, 5));
    return tube;
  }

  @Test
  void aMultiPartStepIsCheckedAgainstThePartTheActionBelongsTo() {
    LabStep part = new PartStep(7, null);
    LabStep wrapper = new MultiPartStep(0, "A3", "Both", "Hint", List.of(), false,
        List.of(part));
    labWith(wrapper, 9);

    assertEquals("That's too much: 9.0 mL, and the most is 7.0 mL. Undo and add less.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void aMultiPartStepWithinItsPartsRangeIsFine() {
    LabStep wrapper = new MultiPartStep(0, "A3", "Both", "Hint", List.of(), false,
        List.of(new PartStep(7, null)));
    labWith(wrapper, 6);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void aReagentIntoATubeThatAlreadyHoldsSolventIsNotFlagged() {
    Vessel tube = labWith(new PartStep(0.6, Substances.NAOH), 6);
    tube.getMixture().add(Substances.ETHER, 6);
    tube.getMixture().add(Substances.NAOH, 0.5);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void tooMuchOfTheNamedReagentIsFlagged() {
    Vessel tube = labWith(new PartStep(0.6, Substances.NAOH), 6);
    tube.getMixture().add(Substances.NAOH, 1);

    assertEquals("That's too much: 20 drops, and the most is 12 drops. Undo and add fewer.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void elevenDropsOfNaohIsFlaggedAndNamesTheTen() {
    Vessel tube = labWith(new PartStep(0.5, Substances.NAOH), 6);
    tube.getMixture().add(Substances.NAOH, 11 * StockBottle.DROP_ML);

    assertEquals("That's too much: 11 drops, and the most is 10 drops. Undo and add fewer.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void tenDropsOfNaohIsFine() {
    Vessel tube = labWith(new PartStep(0.5, Substances.NAOH), 6);
    tube.getMixture().add(Substances.NAOH, 10 * StockBottle.DROP_ML);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void aSolidIsNotReportedInDrops() {
    Vessel tube = labWith(new PartStep(0.1, Substances.ASPIRIN), 6);
    tube.getMixture().add(Substances.ASPIRIN, 0.3);

    assertEquals("That's too much: 0.3 mL, and the most is 0.1 mL. Undo and add less.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  @Test
  void aSolventStepMeasuresOnlyItsOwnLayer() {
    Vessel tube = labWith(new PartStep(7, Substances.ETHER), 6);
    tube.getMixture().add(Substances.ETHER, 5);

    assertNull(new AmountRule().check(lab, addTo(ActionKind.ADD)));
    tube.getMixture().add(Substances.ETHER, 3);
    assertEquals("That's too much: 8.0 mL, and the most is 7.0 mL. Undo and add less.",
        new AmountRule().check(lab, addTo(ActionKind.ADD)));
  }

  /** A weighing step that allows up to 0.205 g on the scale. */
  private static final class WeighStep extends LabStep {
    WeighStep() {
      super(0, "A4", "Weigh 0.200 g", "Hint", List.of(), false);
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
      return new Range(0.195, 0.205);
    }
  }

  private Scale scale;
  private Vessel weighTube;

  private void weighing(LabStep step, boolean tared) {
    lab = new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    scale = new Scale("scale-1", new Vec2(0, 0));
    weighTube = new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    lab.getBench().addItem(scale, new Vec2(1, 1));
    lab.getBench().addItem(weighTube, new Vec2(1, 1));
    lab.getBench().addItem(new ItemFactory().create(
        ItemTemplates.find("unknown_vial").orElseThrow(), "unknown_vial-1"), new Vec2(1, 1));
    scale.place("conical-1");
    if (tared) {
      scale.tare(lab.getBench());
    }
  }

  private String scoop(double grams) {
    new UnknownSample('A', ApiType.ASPIRIN).addTo(weighTube.getMixture(), grams);
    return new AmountRule().check(lab,
        new Action(ActionKind.ADD, "unknown_vial-1", "conical-1", grams, null, ""));
  }

  @Test
  void aMassInRangeIsFine() {
    weighing(new WeighStep(), true);

    assertNull(scoop(0.2));
  }

  @Test
  void exactlyTheMostMassIsFine() {
    weighing(new WeighStep(), true);

    assertNull(scoop(0.205));
  }

  @Test
  void overTheMostMassIsFlaggedWithTheReading() {
    weighing(new WeighStep(), true);

    assertEquals("Too much: 0.215 g", scoop(0.215));
  }

  @Test
  void aMassInAMultiPartStepIsCheckedAgainstItsPart() {
    LabStep wrapper = new MultiPartStep(0, "A4", "Weigh", "Hint", List.of(), false,
        List.of(new WeighStep()));
    weighing(wrapper, true);

    assertEquals("Too much: 0.215 g", scoop(0.215));
  }

  @Test
  void anUntaredScaleIsNotAMistakeForACorrectScoop() {
    weighing(new WeighStep(), false);

    assertNull(scoop(0.2));
  }

  @Test
  void aScoopIntoATubeOffTheScaleIsStillFlagged() {
    weighing(new WeighStep(), true);
    scale.remove();

    assertEquals("Too much: 0.300 g", scoop(0.3));
  }

  @Test
  void addingFromABottleIsNotWeighed() {
    weighing(new WeighStep(), true);
    new UnknownSample('A', ApiType.ASPIRIN).addTo(weighTube.getMixture(), 0.3);
    Action pour = new Action(ActionKind.ADD, "water_bottle-1", "conical-1", 1, null, "");

    assertNull(new AmountRule().check(lab, pour));
  }

  @Test
  void aMissingSourceIsNotWeighed() {
    weighing(new WeighStep(), true);
    Action pour = new Action(ActionKind.ADD, "missing-1", "conical-1", 1, null, "");

    assertNull(new AmountRule().check(lab, pour));
  }
}
