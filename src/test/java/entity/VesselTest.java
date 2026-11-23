package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests a piece of glassware: its label, cover, cold flag, what it weighs, and holding a tube.
 */
class VesselTest {
  private static final double GLASS = 10.0;       // grams of empty glass
  private static final double TOLERANCE = 1e-9;

  private Vessel conicalTube() {
    return new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, GLASS);
  }

  @Test
  void newVesselIsEmptyAndUnlabelled() {
    Vessel tube = conicalTube();
    assertTrue(tube.getMixture().getLayers().isEmpty());
    assertEquals("", tube.getLabel());
    assertFalse(tube.isCovered());
    assertFalse(tube.isCold());
    assertEquals(0, tube.getStirCount());
    assertNull(tube.getHoldingId());
  }

  @Test
  void keepsTypeAndCapacity() {
    Vessel tube = conicalTube();
    assertEquals(ItemType.CONICAL_TUBE, tube.getType());
    assertEquals(15.0, tube.getCapacity());
  }

  @Test
  void setLabelKeepsText() {
    Vessel tube = conicalTube();
    tube.setLabel("A");                           // A2: the batch letter
    assertEquals("A", tube.getLabel());
  }

  @Test
  void coverMakesItCovered() {
    Vessel cylinder = new Vessel("cyl-1", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0), 10, 20);
    cylinder.cover();                             // A3: ether cylinder
    assertTrue(cylinder.isCovered());
  }

  @Test
  void makeColdStaysCold() {
    Vessel tube = conicalTube();
    tube.makeCold();                              // B2: ice bath
    assertTrue(tube.isCold());
  }

  @Test
  void stirCounts() {
    Vessel tube = conicalTube();
    tube.stir();
    tube.stir();
    assertEquals(2, tube.getStirCount());         // B1 needs 5
  }

  @Test
  void emptyVesselWeighsJustTheGlass() {
    assertEquals(GLASS, conicalTube().massG(), TOLERANCE);
  }

  @Test
  void massIsGlassPlusPowder() {
    Vessel tube = conicalTube();
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.200);   // A4 scoop
    assertEquals(GLASS + 0.200, tube.massG(), TOLERANCE);
  }

  @Test
  void liquidIsNotWeighed() {
    Vessel tube = conicalTube();
    tube.getMixture().add(Substances.WATER, 2);
    assertEquals(GLASS, tube.massG(), TOLERANCE);
  }

  @Test
  void naohIsNotWeighed() {
    Vessel tube = conicalTube();
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.200);
    tube.getMixture().add(Substances.NAOH, 0.5);                 // 10 drops, in mL
    assertEquals(GLASS + 0.200, tube.massG(), TOLERANCE);
  }

  @Test
  void dissolvedSampleIsStillWeighed() {
    Vessel tube = conicalTube();
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.200);
    tube.getMixture().add(Substances.ETHER, 4);
    tube.getMixture().mix(Mixture.FULL_MIX);                     // drug + vanillin dissolve
    assertEquals(GLASS + 0.200, tube.massG(), TOLERANCE);
  }

  @Test
  void holdRemembersTheTube() {
    Vessel beaker = new Vessel("beaker-1", ItemType.BEAKER, new Vec2(0, 0), 50, 30);
    beaker.hold("conical-1");                     // A4: tube stands in the beaker
    assertEquals("conical-1", beaker.getHoldingId());
  }

  @Test
  void releaseHeldForgetsIt() {
    Vessel beaker = new Vessel("beaker-1", ItemType.BEAKER, new Vec2(0, 0), 50, 30);
    beaker.hold("conical-1");
    beaker.releaseHeld();
    assertNull(beaker.getHoldingId());
  }
}
