package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the first steps of Task A: put the unknown in the tube, add ether and water, mix,
 * and take the bottom (water) layer out. (NaOH changing the pH comes in Phase 3.)
 */
class MixtureTest {
  private static final double UNKNOWN_ASPIRIN = 1.11;  // 0.200 g of aspirin, in mmol
  private static final double UNKNOWN_VANILLIN = 0.3;  // the contaminant, in mmol

  /** A conical tube with the unknown, 4 mL ether and 2 mL water, not mixed yet. */
  private Mixture tubeBeforeMixing() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, UNKNOWN_ASPIRIN);
    tube.add(Substances.VANILLIN, UNKNOWN_VANILLIN);
    tube.add(Substances.ETHER, 4);
    tube.add(Substances.WATER, 2);
    return tube;
  }

  @Test
  void newTubeIsEmptyAndNeutral() {
    Mixture tube = new Mixture();
    assertTrue(tube.getLayers().isEmpty());
    assertEquals(7, tube.getPh());
  }

  @Test
  void unknownAloneIsJustSolid() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, UNKNOWN_ASPIRIN);
    assertEquals(Phase.SOLID, tube.getLayers().get(0).getPhase());
  }

  @Test
  void waterAndEtherMakeTwoLayersWaterOnBottom() {
    Mixture tube = tubeBeforeMixing();
    assertEquals(2, tube.getLayers().size());
    assertEquals(Phase.AQUEOUS, tube.getLayers().get(0).getPhase());
    assertEquals(2.0, tube.getLayers().get(0).getVolume());
    assertEquals(Phase.ORGANIC, tube.getLayers().get(1).getPhase());
    assertEquals(4.0, tube.getLayers().get(1).getVolume());
  }

  @Test
  void solidStaysUndissolvedUntilMixed() {
    Mixture tube = tubeBeforeMixing();
    double solid = tube.getLayers().get(0).getSolids().get(Substances.ASPIRIN);
    assertEquals(UNKNOWN_ASPIRIN, solid);
  }

  @Test
  void notEnoughPumpsChangesNothing() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(10);
    assertTrue(tube.getLayers().get(1).getDissolved().isEmpty());
  }

  @Test
  void mixingDissolvesUnknownInEther() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    Layer ether = tube.getLayers().get(1);
    double aspirin = ether.getDissolved().get(Substances.ASPIRIN);
    double vanillin = ether.getDissolved().get(Substances.VANILLIN);
    assertEquals(UNKNOWN_ASPIRIN, aspirin);
    assertEquals(UNKNOWN_VANILLIN, vanillin);
    assertTrue(tube.getLayers().get(0).getSolids().isEmpty());
  }

  @Test
  void separateLayerTakesBottomWaterLayerOut() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    Mixture aqueousTube = tube.separateLayer(0);

    assertEquals(1, aqueousTube.getLayers().size());
    assertEquals(Phase.AQUEOUS, aqueousTube.getLayers().get(0).getPhase());
    assertEquals(1, tube.getLayers().size());
    assertEquals(Phase.ORGANIC, tube.getLayers().get(0).getPhase());
  }
}
