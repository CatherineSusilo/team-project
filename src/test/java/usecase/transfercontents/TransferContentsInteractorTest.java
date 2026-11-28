package usecase.transfercontents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.AcidBaseRule;
import entity.ApiType;
import entity.Bench;
import entity.BuchnerFunnel;
import entity.ChemistryEngine;
import entity.ClampStand;
import entity.FiltrationFlask;
import entity.ItemTemplates;
import entity.ItemType;
import entity.Lab;
import entity.Layer;
import entity.Mixture;
import entity.PasteurPipette;
import entity.StockBottle;
import entity.Substance;
import entity.Substances;
import entity.TemplateItem;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.ItemData;
import usecase.LabDataAccessInterface;

/**
 * Tests the transfer use case on a real lab with a few items on its bench, without a screen.
 */
class TransferContentsInteractorTest {
  private static final double TOLERANCE = 1e-9;

  private final FakeLab bench = new FakeLab();
  private TransferContentsOutputData output;
  private String error;
  private final TransferContentsInteractor interactor = new TransferContentsInteractor(
      bench, new TransferContentsOutputBoundary() {
        @Override
        public void prepareSuccessView(TransferContentsOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      },
      new ChemistryEngine(List.of(new AcidBaseRule())));

  /** A lab whose bench holds a few items, each with an id, a type, a capacity and contents. */
  private static class FakeLab implements LabDataAccessInterface {
    private final Lab lab = new Lab(Lab3Definition.steps(), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    private int snapshots;
    private int saves;
    private boolean diskFull;                              // saving to the file fails

    FakeLab() {
      Vessel conical = addVessel("conical", ItemType.CONICAL_TUBE, 15);
      conical.getMixture().add(Substances.ASPIRIN, 0.160);  // after the first mix in Task A
      conical.getMixture().add(Substances.VANILLIN, 0.040);
      conical.getMixture().add(Substances.ETHER, 4);
      conical.getMixture().add(Substances.WATER, 2);
      conical.getMixture().mix(Mixture.FULL_MIX);
      addVessel("emptyConical", ItemType.CONICAL_TUBE, 15);
      addVessel("testTube", ItemType.TEST_TUBE, 15);
      lab.getBench().addItem(new PasteurPipette("pipette", new Vec2(0, 0)), new Vec2(0, 0));
      addVessel("etherCylinder", ItemType.GRADUATED_CYLINDER, 10);
      addVessel("fullEtherCylinder", ItemType.GRADUATED_CYLINDER, 10).getMixture()
          .add(Substances.ETHER, 6);                        // A3: ~6 mL of ether

      addBottle("naohDropper", ItemType.DROPPER, Substances.NAOH);
      addBottle("hclDropper", ItemType.DROPPER, Substances.HCL);
      addBottle("etherBottle", ItemType.SOLVENT_BOTTLE, Substances.ETHER);
      lab.getBench().addItem(new TemplateItem("unknownJar",   // the unknown sample's vial
          ItemTemplates.find("unknown_vial").orElseThrow(), new Vec2(0, 0)), new Vec2(0, 0));
    }

    private Vessel addVessel(String id, ItemType type, double capacity) {
      Vessel vessel = new Vessel(id, type, new Vec2(0, 0), capacity, 0);
      lab.getBench().addItem(vessel, new Vec2(0, 0));
      return vessel;
    }

    void addBottle(String id, ItemType type, Substance substance) {
      lab.getBench().addItem(new StockBottle(id, type, new Vec2(0, 0), substance),
          new Vec2(0, 0));
    }

    Mixture getMixture(String id) {
      return ((Vessel) lab.getBench().getItem(id)).getMixture();
    }

    PasteurPipette getPipette(String id) {
      return (PasteurPipette) lab.getBench().getItem(id);
    }

    Substance getSubstance(String id) {
      return ((StockBottle) lab.getBench().getItem(id)).getSubstance();
    }

    @Override
    public Lab getLab() {
      return lab;
    }

    @Override
    public void saveLab(Lab saved) {
      if (diskFull) {
        throw new UncheckedIOException(new IOException("disk full"));
      }
      saves++;
    }

    @Override
    public void pushSnapshot() {
      snapshots++;
    }

    @Override
    public boolean popSnapshot() {
      return false;
    }

    @Override
    public void markStepStart(int index) {
    }

    @Override
    public boolean rewindToStepStart(int index) {
      return false;
    }
  }

  /** How the screen shows one item after the last successful transfer. */
  private ItemData shown(String id) {
    for (ItemData item : output.getState().getItems()) {
      if (item.getId().equals(id)) {
        return item;
      }
    }
    return null;
  }

  // ----- the hookup: undo point, record, save -----

  @Test
  void aFailedSaveStillShowsTheChangeWithAWarning() {
    bench.diskFull = true;
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    assertEquals("Done, but your progress couldn't be saved.", error);
    assertNotNull(output);                                  // the change is still shown
  }

  @Test
  void aTransferSavesAnUndoPointAndTheLab() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    assertEquals(1, bench.snapshots);                       // Undo can go back to before it
    assertEquals(1, bench.saves);
  }

  @Test
  void aRejectedTransferChangesNothing() {
    interactor.execute(new TransferContentsInputData("hclDropper", "conical", 20,
        TransferUnit.DROPS));                               // HCl isn't for the conical tube
    assertEquals(0, bench.snapshots);
    assertEquals(0, bench.saves);
  }

  @Test
  void theScreenGetsEveryItem() {
    interactor.execute(new TransferContentsInputData("etherBottle", "etherCylinder", 6,
        TransferUnit.ML));
    assertEquals(bench.getLab().getBench().getItems().size(),
        output.getState().getItems().size());
  }

  // ----- stock bottles -----

  @Test
  void tenDropsOfNaohMakesTheTubeBasic() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    assertNull(error);
    assertEquals(AcidBaseRule.BASIC_PH, shown("conical").getPh());
  }

  @Test
  void tenDropsAreStoredAsHalfAMillilitre() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    Layer bottom = bench.getMixture("conical").getLayers().get(0);
    assertEquals(0.5, bottom.getSolids().get(Substances.NAOH), TOLERANCE);
  }

  @Test
  void naohThenThirtyMixesMovesTheDrugIntoTheWater() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    interactor.execute(new TransferContentsInputData("pipette", "conical", 30,
        TransferUnit.MIXES));
    Mixture conical = bench.getMixture("conical");
    assertTrue(conical.getLayers().get(0).getDissolved().containsKey(Substances.ASPIRIN));
    assertEquals(List.of("AQUEOUS", "ORGANIC"), shown("conical").getLayerPhases());
  }

  @Test
  void fillingTheEtherCylinderWorks() {
    interactor.execute(new TransferContentsInputData("etherBottle", "etherCylinder", 6,
        TransferUnit.ML));
    assertNull(error);
    assertEquals(List.of("ORGANIC"), shown("etherCylinder").getLayerPhases());
    assertEquals(6.0, bench.getMixture("etherCylinder").totalVolume(), TOLERANCE);
  }

  @Test
  void fillingTheWaterCylinderFromItsStockWorks() {
    bench.addBottle("waterBottle", ItemType.SOLVENT_BOTTLE, Substances.WATER);
    interactor.execute(new TransferContentsInputData("waterBottle", "etherCylinder", 6,
        TransferUnit.ML));                                  // any cylinder works the same
    assertNull(error);
    assertEquals(List.of("AQUEOUS"), shown("etherCylinder").getLayerPhases());
    assertEquals(Substances.WATER, bench.getSubstance("waterBottle"));  // stock unchanged
  }

  @Test
  void hclIntoTheConicalTubeIsRejectedAndNothingChanges() {
    interactor.execute(new TransferContentsInputData("hclDropper", "conical", 20,
        TransferUnit.DROPS));
    assertEquals("HCl goes into the aqueous test tube.", error);
    assertNull(output);
    assertEquals(7, bench.getMixture("conical").getPh());
  }

  @Test
  void naohIntoTheEtherCylinderIsRejected() {
    interactor.execute(new TransferContentsInputData("naohDropper", "etherCylinder", 10,
        TransferUnit.DROPS));
    assertEquals("NaOH goes into the conical tube.", error);
  }

  @Test
  void droppingOnNothingFails() {
    interactor.execute(new TransferContentsInputData("naohDropper", "bench", 10,
        TransferUnit.DROPS));
    assertEquals("Drop it onto a container.", error);
  }

  @Test
  void zeroAmountFails() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 0,
        TransferUnit.DROPS));
    assertEquals("Choose an amount above 0.", error);
  }

  // ----- the unknown (spatula scoops) -----

  @Test
  void unknownGoesIntoConicalTube() {
    interactor.execute(new TransferContentsInputData("unknownJar", "emptyConical", 0.200,
        TransferUnit.GRAMS));
    assertNull(error);
    Layer powder = bench.getMixture("emptyConical").getLayers().get(0);
    assertEquals(0.160, powder.getSolids().get(Substances.ASPIRIN), TOLERANCE);
    assertEquals(0.040, powder.getSolids().get(Substances.VANILLIN), TOLERANCE);
    assertTrue(shown("emptyConical").hasSolid());
  }

  @Test
  void unknownIntoTestTubeFails() {
    interactor.execute(new TransferContentsInputData("unknownJar", "testTube", 0.200,
        TransferUnit.GRAMS));
    assertEquals("The unknown goes into the conical tube.", error);
    assertTrue(bench.getMixture("testTube").getLayers().isEmpty());
  }

  // ----- pipette: draw and dispense -----

  @Test
  void pipetteDrawsFromEtherCylinder() {
    interactor.execute(new TransferContentsInputData("fullEtherCylinder", "pipette", 2,
        TransferUnit.ML));
    assertNull(error);
    assertEquals(2.0, bench.getMixture("pipette").totalVolume(), TOLERANCE);
    assertEquals(4.0, bench.getMixture("fullEtherCylinder").totalVolume(), TOLERANCE);
  }

  @Test
  void pipetteOverCapacityFails() {
    interactor.execute(new TransferContentsInputData("fullEtherCylinder", "pipette", 2.5,
        TransferUnit.ML));
    assertEquals("That won't fit.", error);
    assertTrue(bench.getMixture("pipette").getLayers().isEmpty());
    assertEquals(6.0, bench.getMixture("fullEtherCylinder").totalVolume(), TOLERANCE);
  }

  @Test
  void pipetteMovesWaterLayerToTestTube() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    interactor.execute(new TransferContentsInputData("pipette", "conical", 30,
        TransferUnit.MIXES));
    interactor.execute(new TransferContentsInputData("conical", "pipette", 2,
        TransferUnit.ML));                                  // A8: draw the water layer
    interactor.execute(new TransferContentsInputData("pipette", "testTube", 2,
        TransferUnit.ML));                                  // empty it into the test tube

    assertNull(error);
    Layer water = bench.getMixture("testTube").getLayers().get(0);
    assertEquals("AQUEOUS", water.getPhase().name());
    assertTrue(water.getDissolved().containsKey(Substances.ASPIRIN));
    assertEquals(AcidBaseRule.BASIC_PH, shown("testTube").getPh());
    assertTrue(bench.getMixture("pipette").getLayers().isEmpty());
  }

  @Test
  void fortyDropsFillThePipetteExactly() {
    bench.addBottle("waterBottle", ItemType.SOLVENT_BOTTLE, Substances.WATER);
    for (int i = 0; i < 40; i++) {                          // one drop at a time
      interactor.execute(new TransferContentsInputData("waterBottle", "pipette", 1,
          TransferUnit.DROPS));
    }
    assertNull(error);                                      // the 40th drop still fits
    assertEquals(2.0, bench.getMixture("pipette").totalVolume(), TOLERANCE);
  }

  @Test
  void containerToContainerWithoutPipetteFails() {
    interactor.execute(new TransferContentsInputData("fullEtherCylinder", "emptyConical", 4,
        TransferUnit.ML));
    assertEquals("Use a pipette to move liquid.", error);
  }

  @Test
  void pouringFromATubeWithOnlyPowderFails() {
    interactor.execute(new TransferContentsInputData("unknownJar", "emptyConical", 0.200,
        TransferUnit.GRAMS));                               // just powder, no liquid
    error = null;
    interactor.execute(new TransferContentsInputData("emptyConical", "pipette", 1,
        TransferUnit.ML));
    assertEquals("There's nothing in that to pour.", error);
    assertTrue(bench.getMixture("pipette").getLayers().isEmpty());
  }

  @Test
  void pouringFromAnEmptyPipetteFails() {
    interactor.execute(new TransferContentsInputData("pipette", "testTube", 1,
        TransferUnit.ML));
    assertEquals("There's nothing in that to pour.", error);
  }

  // ----- A5: ether + water, mix 15 (pipette gets used) -----

  /** A3-A5: the 0.200 g unknown, then 4 mL ether and 2 mL water, in the empty conical tube. */
  private void unknownEtherAndWater() {
    bench.addBottle("waterBottle", ItemType.SOLVENT_BOTTLE, Substances.WATER);
    interactor.execute(new TransferContentsInputData("unknownJar", "emptyConical", 0.200,
        TransferUnit.GRAMS));
    interactor.execute(new TransferContentsInputData("etherBottle", "emptyConical", 4,
        TransferUnit.ML));
    interactor.execute(new TransferContentsInputData("waterBottle", "emptyConical", 2,
        TransferUnit.ML));
  }

  @Test
  void etherAndWaterMakeTwoLayers() {
    unknownEtherAndWater();
    assertNull(error);
    assertEquals(List.of("AQUEOUS", "ORGANIC"), shown("emptyConical").getLayerPhases());
    assertEquals(6.0, bench.getMixture("emptyConical").totalVolume(), TOLERANCE);
  }

  @Test
  void mixingFifteenTimesIsCloudyAndCounted() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));
    assertFalse(shown("emptyConical").isSettled());         // the mixing PNG
    assertEquals(15, bench.getMixture("emptyConical").getMixCount());   // MixStep: "15 / 30"
    assertTrue(shown("emptyConical").hasSolid());           // not mixed enough to dissolve
  }

  @Test
  void mixingMarksThePipetteUsed() {
    interactor.execute(new TransferContentsInputData("pipette", "conical", 15,
        TransferUnit.MIXES));
    assertFalse(bench.getPipette("pipette").isClean());
  }

  @Test
  void mixingPipetteCanKeepMixingTheSameTube() {
    interactor.execute(new TransferContentsInputData("pipette", "conical", 1,
        TransferUnit.MIXES));                               // pump 1
    assertTrue(bench.getPipette("pipette").canMix("conical"));   // pump 2 and A7 are fine
  }

  @Test
  void sameMixingPipetteThroughA8AndA9() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 10,
        TransferUnit.DROPS));
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 30,
        TransferUnit.MIXES));                               // A7
    interactor.execute(new TransferContentsInputData("emptyConical", "pipette", 1.5,
        TransferUnit.ML));                                  // A8: draw the water layer
    assertTrue(bench.getPipette("pipette").canMix("emptyConical"));   // A9 can mix again
  }

  @Test
  void drawingEtherMarksThePipetteUsed() {
    interactor.execute(new TransferContentsInputData("etherBottle", "pipette", 2,
        TransferUnit.ML));
    assertNull(error);
    assertFalse(bench.getPipette("pipette").isClean());
  }

  @Test
  void failedTransferLeavesThePipetteClean() {
    interactor.execute(new TransferContentsInputData("fullEtherCylinder", "pipette", 2.5,
        TransferUnit.ML));                                  // won't fit
    assertTrue(bench.getPipette("pipette").isClean());
  }

    @Test
  void mixingAnEmptyTubeFails() {
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));
    assertEquals("There's nothing to mix yet.", error);
    assertTrue(bench.getPipette("pipette").isClean());     // nothing happened
  }

  @Test
  void mixingPowderAloneFails() {
    interactor.execute(new TransferContentsInputData("unknownJar", "emptyConical", 0.200,
        TransferUnit.GRAMS));                               // A4: just the powder
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));
    assertEquals("There's nothing to mix yet.", error);
    assertEquals(0, bench.getMixture("emptyConical").getMixCount());
  }
  
  // ----- A6: 10 drops of NaOH -----

  @Test
  void tenDropsOfNaohAddHalfAMillilitre() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 10,
        TransferUnit.DROPS));
    assertEquals(0.5, bench.getMixture("emptyConical").amountOf(Substances.NAOH), TOLERANCE);
  }

  @Test
  void tenSingleClicksOfNaohAddExactlyHalfAMillilitre() {
    unknownEtherAndWater();
    for (int i = 0; i < 10; i++) {                          // the A6 UI: one drop per click
      interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 1,
          TransferUnit.DROPS));
    }
    assertEquals(0.5, bench.getMixture("emptyConical").amountOf(Substances.NAOH));
  }

  // ----- A7: mix 30, everything dissolves -----

  @Test
  void thirtyPumpsAfterNaohDissolveEverything() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));                               // A5
    interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 10,
        TransferUnit.DROPS));                               // A6
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 30,
        TransferUnit.MIXES));                               // A7

    assertNull(error);
    assertFalse(shown("emptyConical").hasSolid());          // all dissolved
    assertEquals(AcidBaseRule.BASIC_PH, shown("emptyConical").getPh());
    List<Layer> layers = bench.getMixture("emptyConical").getLayers();
    assertTrue(layers.get(0).getDissolved().containsKey(Substances.ASPIRIN));   // water
    assertTrue(layers.get(1).getDissolved().containsKey(Substances.VANILLIN));  // ether
  }

  @Test
  void naohResetsTheCountSoFifteenMoreIsNotEnough() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));
    interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 10,
        TransferUnit.DROPS));
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));                               // 15 + 15, but NaOH reset it
    assertEquals(15, bench.getMixture("emptyConical").getMixCount());   // MixStep: "15 / 30"
    assertTrue(shown("emptyConical").hasSolid());
  }

  // ----- B3: pouring onto the Büchner funnel -----

  /**
   * B2/B3: a flask clamped to the stand with a funnel (and maybe paper) on top, and the test
   * tube after B1: 3 mL of water with 0.150 g of the drug as powder.
   */
  private BuchnerFunnel filtrationSetUp(boolean paper) {
    ClampStand stand = new ClampStand("stand", new Vec2(0, 0));
    FiltrationFlask flask = new FiltrationFlask("flask", new Vec2(0, 0), 250, 150);
    BuchnerFunnel funnel = new BuchnerFunnel("funnel", new Vec2(0, 0), 10, 60);
    Bench lab = bench.getLab().getBench();
    lab.addItem(stand, new Vec2(0, 0));
    lab.addItem(flask, new Vec2(0, 0));
    lab.addItem(funnel, new Vec2(0, 0));
    stand.clamp(flask);
    flask.placeFunnel("funnel");
    if (paper) {
      funnel.addPaper();
    }
    Mixture tube = bench.getMixture("testTube");
    tube.add(Substances.WATER, 3);
    tube.add(Substances.ASPIRIN, 0.150);
    return funnel;
  }

  /** One rinse: 1 mL of cold water into the test tube, then the tube onto the funnel again. */
  private void rinse() {
    bench.getMixture("testTube").add(Substances.WATER, 1);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 1, TransferUnit.ML));
  }

  @Test
  void pouringOntoTheFunnelSendsTheLiquidToTheFlask() {
    filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertNull(error);
    assertEquals(3.0, bench.getMixture("flask").totalVolume(), TOLERANCE);   // the filtrate
    assertEquals(0, bench.getMixture("testTube").totalVolume(), TOLERANCE);
    assertEquals(0, bench.getMixture("funnel").totalVolume(), TOLERANCE);    // nothing pools
  }

  @Test
  void tippingTheTubeOutPutsAllTheSolidOnThePaper() {
    filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertEquals(0.150, bench.getMixture("funnel").amountOf(Substances.ASPIRIN), TOLERANCE);
    assertEquals(0, bench.getMixture("testTube").amountOf(Substances.ASPIRIN), TOLERANCE);
    assertTrue(shown("funnel").hasSolid());                 // buchner-funnel--wet-solid PNG
  }

  @Test
  void theFirstPourIsNotARinse() {
    BuchnerFunnel funnel = filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertEquals(0, funnel.getRinses());
  }

  @Test
  void eachRinseIsCounted() {
    BuchnerFunnel funnel = filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    rinse();
    assertEquals(1, funnel.getRinses());
    rinse();
    assertEquals(2, funnel.getRinses());
    assertEquals(5.0, bench.getMixture("flask").totalVolume(), TOLERANCE);   // 3 + 1 + 1 mL
  }

  @Test
  void pipetteDropsOntoTheFunnelAreNotARinse() {
    BuchnerFunnel funnel = filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    bench.getMixture("pipette").add(Substances.ETHER, 1);   // B4: cold ether in a pipette
    interactor.execute(new TransferContentsInputData("pipette", "funnel", 3,
        TransferUnit.DROPS));
    assertNull(error);
    assertEquals(0, funnel.getRinses());
  }

  @Test
  void withoutPaperTheSolidGoesThrough() {
    filtrationSetUp(false);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertEquals(0, bench.getMixture("funnel").amountOf(Substances.ASPIRIN), TOLERANCE);
    assertEquals(0.150, bench.getMixture("flask").amountOf(Substances.ASPIRIN), TOLERANCE);
  }

  @Test
  void aFunnelThatIsNotSetUpFails() {
    filtrationSetUp(true);
    ((FiltrationFlask) bench.getLab().getBench().getItem("flask")).removeFunnel();
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertEquals("Set up the filtration first.", error);
    assertEquals(0.150, bench.getMixture("testTube").amountOf(Substances.ASPIRIN), TOLERANCE);
  }

  @Test
  void pouringNothingOntoTheFunnelFails() {
    filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("emptyConical", "funnel", 1,
        TransferUnit.ML));
    assertEquals("There's nothing in that to pour.", error);
  }

  @Test
  void pouringOntoTheFunnelSavesAnUndoPoint() {
    filtrationSetUp(true);
    interactor.execute(new TransferContentsInputData("testTube", "funnel", 3, TransferUnit.ML));
    assertEquals(1, bench.snapshots);
  }
}
