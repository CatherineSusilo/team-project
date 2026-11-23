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
  // ----- mixing pipette (A5, A7) -----

  @Test
  void newPipetteCanMixAnyTube() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    assertTrue(pipette.canMix("conical-1"));
  }

  @Test
  void mixingPipetteCanKeepMixingItsTube() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    pipette.markMixing("conical-1");                             // A5, pump 1
    assertFalse(pipette.isClean());
    assertTrue(pipette.canMix("conical-1"));                     // pump 2, and A7 later
    assertFalse(pipette.canMix("conical-2"));                    // but not another tube
  }

  @Test
  void usedPipetteNeverBecomesTheMixingPipette() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    pipette.markUsed();                                          // it carried ether
    pipette.markMixing("conical-1");
    assertFalse(pipette.canMix("conical-1"));
  }

  @Test
  void drawingTheWaterLayerKeepsItTheMixingPipette() {
    PasteurPipette pipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    pipette.markMixing("conical-1");                             // A5, A7
    pipette.markUsed();                                          // A8: draws the water layer
    assertTrue(pipette.canMix("conical-1"));                     // A9: mixes again, same pipette
  }
}
