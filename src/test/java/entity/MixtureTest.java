package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the conical tube through Task A: the unknown, ether and water go in, it is mixed,
 * NaOH moves the drug into the water, and the pipette draws the water layer out into the
 * test tube. Solids are in grams, liquids in mL.
 */
class MixtureTest {
  private static final double DRUG = 0.160;      // grams of aspirin in a 0.200 g scoop
  private static final double VANILLIN = 0.040;  // grams of vanillin in the same scoop
  private static final double TOLERANCE = 1e-9; // decimals can be off by a tiny bit

  /** A conical tube with the unknown, 4 mL ether and 2 mL water, not mixed yet. */
  private Mixture tubeBeforeMixing() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, DRUG);
    tube.add(Substances.VANILLIN, VANILLIN);
    tube.add(Substances.ETHER, 4);
    tube.add(Substances.WATER, 2);
    return tube;
  }

  /** The same tube after 10 drops of NaOH (0.5 mL) and 30 pumps: the drug is in the water. */
  private Mixture tubeAfterNaoh() {
    Mixture tube = tubeBeforeMixing();
    ChemistryEngine chemistry = new ChemistryEngine(List.of(new AcidBaseRule()));
    chemistry.addReagent(tube, Substances.NAOH, 0.5);
    tube.mix(Mixture.FULL_MIX);
    return tube;
  }

  // ----- what was already here -----

  @Test
  void newTubeIsEmptyAndNeutral() {
    Mixture tube = new Mixture();
    assertTrue(tube.getLayers().isEmpty());
    assertEquals(7, tube.getPh());
  }

  @Test
  void unknownAloneIsJustSolid() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, DRUG);
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
    assertEquals(DRUG, solid);
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
    assertEquals(DRUG, ether.getDissolved().get(Substances.ASPIRIN));
    assertEquals(VANILLIN, ether.getDissolved().get(Substances.VANILLIN));
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

  // ----- mixCount -----

  @Test
  void pumpsAddUp() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(10);
    tube.mix(5);
    assertEquals(15, tube.getMixCount());
  }

  @Test
  void fifteenPlusFifteenPumpsSortsLayers() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(15);                                                // A5
    assertTrue(tube.getLayers().get(1).getDissolved().isEmpty()); // not yet
    tube.mix(15);                                                // 30 in total
    assertEquals(DRUG, tube.getLayers().get(1).getDissolved().get(Substances.ASPIRIN));
  }

  @Test
  void addingResetsMixCount() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(10);
    tube.add(Substances.WATER, 1);
    assertEquals(0, tube.getMixCount());
  }

  // ----- settling -----

  @Test
  void newTubeIsSettled() {
    assertTrue(new Mixture().isSettled());
  }

  @Test
  void notSettledRightAfterMixing() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    assertFalse(tube.isSettled());                               // the "mixing" PNG (A7)
  }

  @Test
  void evenAFewPumpsMakeItCloudy() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(5);
    assertFalse(tube.isSettled());
  }

  @Test
  void lessThanAMinuteStaysCloudy() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    tube.advance(0.5);
    assertFalse(tube.isSettled());
  }

  @Test
  void mixingAgainMakesItCloudyAgain() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    tube.advance(Mixture.SETTLE_MINUTES);
    tube.mix(5);                                                 // cloudy again
    assertFalse(tube.isSettled());
  }

  @Test
  void settledAfterOneMinute() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);
    tube.advance(Mixture.SETTLE_MINUTES);
    assertTrue(tube.isSettled());
  }

  // ----- drawFromBottom -----

  @Test
  void drawPartOfWaterLayer() {
    Mixture tube = tubeAfterNaoh();                              // 2 mL water holds the drug
    Mixture pipette = tube.drawFromBottom(1);

    Layer taken = pipette.getLayers().get(0);
    assertEquals(Phase.AQUEOUS, taken.getPhase());
    assertEquals(1.0, taken.getVolume(), TOLERANCE);
    assertEquals(DRUG / 2, taken.getDissolved().get(Substances.ASPIRIN), TOLERANCE); // half

    Layer left = tube.getLayers().get(0);                        // 1 mL of water stays behind
    assertEquals(Phase.AQUEOUS, left.getPhase());
    assertEquals(1.0, left.getVolume(), TOLERANCE);
    assertEquals(DRUG / 2, left.getDissolved().get(Substances.ASPIRIN), TOLERANCE);
  }

  @Test
  void drawMoreThanBottomLayerStopsAtIt() {
    Mixture tube = tubeAfterNaoh();
    Mixture pipette = tube.drawFromBottom(3);                    // asks for more than 2 mL

    assertEquals(1, pipette.getLayers().size());
    assertEquals(Phase.AQUEOUS, pipette.getLayers().get(0).getPhase());
    assertEquals(2.0, pipette.getLayers().get(0).getVolume(), TOLERANCE);

    assertEquals(1, tube.getLayers().size());                    // only the ether is left
    assertEquals(Phase.ORGANIC, tube.getLayers().get(0).getPhase());
    assertEquals(4.0, tube.getLayers().get(0).getVolume(), TOLERANCE);
  }

  @Test
  void drawingAllTheLiquidKeepsThePowder() {
    Mixture testTube = new Mixture();                            // e.g. after HCl: solid drug
    testTube.add(Substances.WATER, 2);
    testTube.add(Substances.ASPIRIN, DRUG);                      // sits as powder
    testTube.drawFromBottom(5);                                  // draw off all the water
    assertEquals(Phase.SOLID, testTube.getLayers().get(0).getPhase());
    assertEquals(DRUG, testTube.getLayers().get(0).getSolids().get(Substances.ASPIRIN),
        TOLERANCE);                                              // still there to filter
  }

  @Test
  void drawingFromPowderOnlyTakesNothing() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, DRUG);                          // no liquid at all
    Mixture pipette = tube.drawFromBottom(1);
    assertTrue(pipette.getLayers().isEmpty());
    assertEquals(DRUG, tube.getLayers().get(0).getSolids().get(Substances.ASPIRIN));
  }

  @Test
  void drawKeepsPh() {
    Mixture tube = tubeAfterNaoh();
    assertEquals(13, tube.drawFromBottom(1).getPh());
  }

  @Test
  void drawFromEtherCylinder() {
    Mixture cylinder = new Mixture();
    cylinder.add(Substances.ETHER, 6);
    Mixture pipette = cylinder.drawFromBottom(2);
    assertEquals(Phase.ORGANIC, pipette.getLayers().get(0).getPhase());
    assertEquals(2.0, pipette.totalVolume(), TOLERANCE);
    assertEquals(4.0, cylinder.totalVolume(), TOLERANCE);       // the "part used" PNG
  }

  // ----- pour -----

  @Test
  void pourMovesWaterIntoEmptyTube() {
    Mixture tube = tubeAfterNaoh();
    Mixture testTube = new Mixture();
    testTube.pour(tube.drawFromBottom(2));

    assertEquals(1, testTube.getLayers().size());
    assertEquals(Phase.AQUEOUS, testTube.getLayers().get(0).getPhase());
    assertEquals(DRUG, testTube.getLayers().get(0).getDissolved().get(Substances.ASPIRIN),
        TOLERANCE);
    assertEquals(13, testTube.getPh());                          // took the source's pH
  }

  @Test
  void pourCombinesWaterLayers() {
    Mixture tube = tubeAfterNaoh();
    Mixture testTube = new Mixture();
    testTube.pour(tube.drawFromBottom(1));                       // first pipette
    testTube.pour(tube.drawFromBottom(1));                       // second pipette

    assertEquals(1, testTube.getLayers().size());                // still one water layer
    assertEquals(2.0, testTube.totalVolume(), TOLERANCE);
    assertEquals(DRUG, testTube.getLayers().get(0).getDissolved().get(Substances.ASPIRIN),
        TOLERANCE);
  }

  @Test
  void pouringResetsMixCount() {
    Mixture tube = tubeBeforeMixing();
    tube.mix(Mixture.FULL_MIX);                                  // A7: 30 pumps
    Mixture pipette = new Mixture();
    pipette.add(Substances.WATER, 2);
    tube.pour(pipette);                                          // A9: wash water in
    assertEquals(0, tube.getMixCount());                         // has to mix 30 again
  }

  @Test
  void emptiedPipetteDoesNotCarryItsOldPh() {
    Mixture pipette = new Mixture();
    pipette.pour(tubeAfterNaoh().drawFromBottom(2));             // carries the pH 13 water
    new Mixture().pour(pipette.drawFromBottom(2));               // empties it into a tube

    Mixture cylinder = new Mixture();
    cylinder.add(Substances.WATER, 5);
    pipette.pour(cylinder.drawFromBottom(2));                    // now plain water
    Mixture beaker = new Mixture();
    beaker.pour(pipette.drawFromBottom(2));
    assertEquals(7, beaker.getPh());                             // no NaOH, so not basic
  }

  @Test
  void pourEmptiesTheSource() {
    Mixture pipette = new Mixture();
    pipette.add(Substances.WATER, 2);
    Mixture tube = new Mixture();
    tube.pour(pipette);
    assertTrue(pipette.getLayers().isEmpty());
    assertEquals(2.0, tube.totalVolume(), TOLERANCE);
  }

  @Test
  void waterIntoBasicTubeStaysBasic() {
    Mixture tube = tubeAfterNaoh();                              // pH 13
    Mixture pipette = new Mixture();
    pipette.add(Substances.WATER, 2);                            // plain water, pH 7
    tube.pour(pipette);
    assertEquals(13, tube.getPh());
  }

  // ----- totalVolume -----

  @Test
  void totalVolumeAddsLayers() {
    assertEquals(6.0, tubeBeforeMixing().totalVolume(), TOLERANCE);
  }
}
