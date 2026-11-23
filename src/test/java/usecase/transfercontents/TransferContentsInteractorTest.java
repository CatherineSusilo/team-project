package usecase.transfercontents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.AcidBaseRule;
import entity.ApiType;
import entity.ChemistryEngine;
import entity.ItemType;
import entity.Layer;
import entity.Mixture;
import entity.PasteurPipette;
import entity.Substance;
import entity.Substances;
import entity.UnknownSample;
import entity.Vec2;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import usecase.TransferContentsDataAccessInterface;

/**
 * Tests the transfer use case with a fake bench, so no real items or screen are needed.
 */
class TransferContentsInteractorTest {
  private static final double TOLERANCE = 1e-9;

  private final FakeBench bench = new FakeBench();
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

  /** A pretend bench: a few items with an id, a type, a capacity and contents. */
  private static class FakeBench implements TransferContentsDataAccessInterface {
    private final Map<String, Mixture> mixtures = new HashMap<>();
    private final Map<String, Substance> substances = new HashMap<>();
    private final Map<String, ItemType> types = new HashMap<>();
    private final Map<String, Double> capacities = new HashMap<>();
    private final Map<String, PasteurPipette> pipettes = new HashMap<>();
    private final UnknownSample unknown = new UnknownSample('A', ApiType.ASPIRIN);

    FakeBench() {
      Mixture conical = new Mixture();                     // after the first mix in Task A
      conical.add(Substances.ASPIRIN, 0.160);
      conical.add(Substances.VANILLIN, 0.040);
      conical.add(Substances.ETHER, 4);
      conical.add(Substances.WATER, 2);
      conical.mix(Mixture.FULL_MIX);
      addVessel("conical", ItemType.CONICAL_TUBE, 15, conical);
      addVessel("emptyConical", ItemType.CONICAL_TUBE, 15, new Mixture());
      addVessel("testTube", ItemType.TEST_TUBE, 15, new Mixture());
      addVessel("pipette", ItemType.PIPETTE, 2, new Mixture());
      addVessel("etherCylinder", ItemType.GRADUATED_CYLINDER, 10, new Mixture());

      Mixture fullEther = new Mixture();                   // A3: ~6 mL of ether
      fullEther.add(Substances.ETHER, 6);
      addVessel("fullEtherCylinder", ItemType.GRADUATED_CYLINDER, 10, fullEther);

      substances.put("naohDropper", Substances.NAOH);
      substances.put("hclDropper", Substances.HCL);
      substances.put("etherBottle", Substances.ETHER);
    }

    private void addVessel(String id, ItemType type, double capacity, Mixture mixture) {
      Mixture contents = mixture;
      if (type == ItemType.PIPETTE) {
        PasteurPipette pipette = new PasteurPipette(id, new Vec2(0, 0));
        pipettes.put(id, pipette);
        contents = pipette.getMixture();                   // a real pipette, so it can be used
      }
      mixtures.put(id, contents);
      types.put(id, type);
      capacities.put(id, capacity);
    }

    @Override
    public Mixture getMixture(String itemId) {
      return mixtures.get(itemId);
    }

    @Override
    public Substance getSubstance(String itemId) {
      return substances.get(itemId);
    }

    @Override
    public ItemType getType(String itemId) {
      return types.get(itemId);
    }

    @Override
    public UnknownSample getUnknown(String itemId) {
      if (itemId.equals("unknownJar")) {
        return unknown;
      }
      return null;
    }

    @Override
    public double getCapacity(String itemId) {
      return capacities.getOrDefault(itemId, 0.0);
    }

    @Override
    public PasteurPipette getPipette(String itemId) {
      return pipettes.get(itemId);
    }
  }

  // ----- stock bottles -----

  @Test
  void tenDropsOfNaohMakesTheTubeBasic() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    assertNull(error);
    assertEquals(AcidBaseRule.BASIC_PH, output.getPh());
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
    assertEquals(List.of("AQUEOUS", "ORGANIC"), output.getLayerPhases());
  }

  @Test
  void fillingTheEtherCylinderWorks() {
    interactor.execute(new TransferContentsInputData("etherBottle", "etherCylinder", 6,
        TransferUnit.ML));
    assertNull(error);
    assertEquals(List.of("ORGANIC"), output.getLayerPhases());
    assertEquals(6.0, bench.getMixture("etherCylinder").totalVolume(), TOLERANCE);
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
    assertTrue(output.hasSolid());
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
    assertEquals(AcidBaseRule.BASIC_PH, output.getPh());
    assertTrue(bench.getMixture("pipette").getLayers().isEmpty());
  }

  @Test
  void fortyDropsFillThePipetteExactly() {
    bench.substances.put("waterBottle", Substances.WATER);
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
    bench.substances.put("waterBottle", Substances.WATER);
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
    assertEquals(List.of("AQUEOUS", "ORGANIC"), output.getLayerPhases());
    assertEquals(6.0, bench.getMixture("emptyConical").totalVolume(), TOLERANCE);
  }

  @Test
  void mixingFifteenTimesIsCloudyAndCounted() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("pipette", "emptyConical", 15,
        TransferUnit.MIXES));
    assertFalse(output.isSettled());                        // the mixing PNG
    assertEquals("Mixed. 15 pumps so far.", output.getMessage());
    assertTrue(output.hasSolid());                          // not mixed enough to dissolve
  }

  @Test
  void mixingMarksThePipetteUsed() {
    interactor.execute(new TransferContentsInputData("pipette", "conical", 15,
        TransferUnit.MIXES));
    assertFalse(bench.getPipette("pipette").isClean());
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
  // ----- A6: 10 drops of NaOH -----

  @Test
  void tenDropsOfNaohAddHalfAMillilitre() {
    unknownEtherAndWater();
    interactor.execute(new TransferContentsInputData("naohDropper", "emptyConical", 10,
        TransferUnit.DROPS));
    assertEquals(0.5, bench.getMixture("emptyConical").amountOf(Substances.NAOH), TOLERANCE);
  }
}