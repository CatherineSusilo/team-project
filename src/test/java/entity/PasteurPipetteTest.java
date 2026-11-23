package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the Pasteur pipette: a small 2 mL vessel that starts clean.
 */
class PasteurPipetteTest {

  @Test
  void newPipetteIsCleanAndEmpty() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    assertTrue(pipette.isClean());
    assertTrue(pipette.getMixture().getLayers().isEmpty());
  }

  @Test
  void capacityIsTwoMillilitres() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    assertEquals(2.0, pipette.getCapacity());
    assertEquals(ItemType.PIPETTE, pipette.getType());
  }

  @Test
  void markUsedMakesItDirty() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    pipette.markUsed();
    assertFalse(pipette.isClean());
  }
}
