package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the stock bottles: they keep their substance, and a drop is 0.05 mL.
 */
class StockBottleTest {

  @Test
  void keepsItsSubstance() {
    StockBottle naoh = new StockBottle("naoh-bottle", ItemType.DROPPER, new Vec2(0, 0),
        Substances.NAOH);
    assertEquals(Substances.NAOH, naoh.getSubstance());
    assertEquals(ItemType.DROPPER, naoh.getType());
  }

  @Test
  void tenDropsIsHalfAMillilitre() {
    assertEquals(0.5, 10 * StockBottle.DROP_ML, 1e-9);
  }
}
