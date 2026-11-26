package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EarlyHclRuleTest {

  private static final String MESSAGE = "HCl is added in Task B. Undo and add NaOH first.";

  private final Lab lab;

  EarlyHclRuleTest() {
    List<LabStep> steps = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      steps.add(new CheckStep(i, "step " + i, "", "", List.of(), false, l -> true, a -> true));
    }
    lab = new Lab(steps, new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
    lab.getBench().addItem(new StockBottle("hcl_bottle-1", ItemType.DROPPER, new Vec2(0, 0),
        Substances.HCL), new Vec2(1, 1));
    lab.getBench().addItem(new StockBottle("naoh_bottle-1", ItemType.DROPPER, new Vec2(0, 0),
        Substances.NAOH), new Vec2(1, 1));
    lab.getBench().addItem(new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6),
        new Vec2(1, 1));
  }

  // Task B starts at step 2 in this three-step lab.
  private String add(String bottleId) {
    return new EarlyHclRule(2).check(lab,
        new Action(ActionKind.ADD, bottleId, "conical-1", 1, null, ""));
  }

  @Test
  void hclBeforeTaskBIsFlagged() {
    assertEquals(MESSAGE, add("hcl_bottle-1"));
  }

  @Test
  void hclIntoTheConicalTubeShowsThisMessageNotTheWrongTargetOne() {
    assertEquals(MESSAGE, add("hcl_bottle-1"));
    assertEquals("HCl goes into the aqueous test tube.", new WrongTargetRule().check(lab,
        new Action(ActionKind.ADD, "hcl_bottle-1", "conical-1", 1, null, "")));
  }

  @Test
  void hclInTaskBIsFine() {
    lab.advance();
    lab.advance();

    assertNull(add("hcl_bottle-1"));
  }

  @Test
  void naohBeforeTaskBIsFine() {
    assertNull(add("naoh_bottle-1"));
  }

  @Test
  void onlyAddingIsChecked() {
    Action mix = new Action(ActionKind.MIX, "hcl_bottle-1", "conical-1", 1, null, "");

    assertNull(new EarlyHclRule(2).check(lab, mix));
  }

  @Test
  void aMissingBottleIsIgnored() {
    assertNull(add("missing-1"));
  }
}
