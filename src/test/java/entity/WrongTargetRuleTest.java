package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class WrongTargetRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A6", "Add", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));

  WrongTargetRuleTest() {
    Bench bench = lab.getBench();
    bench.addItem(new StockBottle("naoh-1", ItemType.DROPPER, new Vec2(0, 0),
        Substances.NAOH), new Vec2(1, 1));
    bench.addItem(new StockBottle("hcl-1", ItemType.DROPPER, new Vec2(0, 0),
        Substances.HCL), new Vec2(1, 1));
    bench.addItem(new StockBottle("ether-1", ItemType.SOLVENT_BOTTLE, new Vec2(0, 0),
        Substances.ETHER), new Vec2(1, 1));
    bench.addItem(new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6),
        new Vec2(1, 1));
    bench.addItem(new Vessel("test-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8),
        new Vec2(1, 1));
    bench.addItem(new Vessel("cylinder-1", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0), 10, 25),
        new Vec2(1, 1));
    bench.addItem(new Vessel("beaker-1", ItemType.BEAKER, new Vec2(0, 0), 100, 30),
        new Vec2(1, 1));
  }

  private String check(String sourceId, String targetId) {
    return new WrongTargetRule().check(lab,
        new Action(ActionKind.ADD, sourceId, targetId, 1, null, ""));
  }

  @Test
  void naohIntoTheConicalTubeIsFine() {
    assertNull(check("naoh-1", "conical-1"));
  }

  @Test
  void naohIntoTheTestTubeIsFlagged() {
    assertEquals("NaOH goes into the conical tube.", check("naoh-1", "test-1"));
  }

  @Test
  void hclIntoTheConicalTubeIsFlagged() {
    assertEquals("HCl goes into the aqueous test tube.", check("hcl-1", "conical-1"));
  }

  @Test
  void hclIntoTheTestTubeIsFine() {
    assertNull(check("hcl-1", "test-1"));
  }

  @Test
  void aSolventMayGoIntoTheConicalTubeOrItsCylinder() {
    assertNull(check("ether-1", "conical-1"));
    assertNull(check("ether-1", "cylinder-1"));
  }

  @Test
  void aSolventIntoAnythingElseIsFlaggedWithItsName() {
    assertEquals("Diethyl ether goes into the conical tube or its graduated cylinder.",
        check("ether-1", "beaker-1"));
  }

  @Test
  void onlyAddingFromAStockBottleIsChecked() {
    Action move = new Action(ActionKind.MOVE_LIQUID, "conical-1", "test-1", 1, null, "");
    assertNull(new WrongTargetRule().check(lab, move));
    assertNull(check("conical-1", "beaker-1"));
    assertNull(check("naoh-1", "missing-1"));
  }
}
