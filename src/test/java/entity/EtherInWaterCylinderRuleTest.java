package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class EtherInWaterCylinderRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A3", "Measure", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel cylinder = new Vessel("cylinder-1", ItemType.GRADUATED_CYLINDER,
      new Vec2(0, 0), 25, 30);

  EtherInWaterCylinderRuleTest() {
    lab.getBench().addItem(cylinder, new Vec2(1, 1));
    lab.getBench().addItem(new StockBottle("ether_bottle-1", ItemType.SOLVENT_BOTTLE,
        new Vec2(0, 0), Substances.ETHER), new Vec2(1, 1));
    lab.getBench().addItem(new StockBottle("water_bottle-1", ItemType.SOLVENT_BOTTLE,
        new Vec2(0, 0), Substances.WATER), new Vec2(1, 1));
  }

  private String add(String bottleId) {
    return new EtherInWaterCylinderRule().check(lab,
        new Action(ActionKind.ADD, bottleId, "cylinder-1", 6, null, ""));
  }

  @Test
  void etherIntoTheWaterCylinderIsFlagged() {
    cylinder.setLabel("water");

    assertEquals("Et2O goes in the cylinder labeled ether.", add("ether_bottle-1"));
  }

  @Test
  void etherIntoTheEtherCylinderIsFine() {
    cylinder.setLabel("ether");

    assertNull(add("ether_bottle-1"));
  }

  @Test
  void waterIntoTheWaterCylinderIsFine() {
    cylinder.setLabel("water");

    assertNull(add("water_bottle-1"));
  }

  @Test
  void etherIntoAnUnlabelledCylinderIsNotThisRulesBusiness() {
    assertNull(add("ether_bottle-1"));
  }
}
