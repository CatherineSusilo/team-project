package usecase.transfercontents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.AcidBaseRule;
import entity.ChemistryEngine;
import entity.ItemType;
import entity.Mixture;
import entity.Substance;
import entity.Substances;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import usecase.TransferContentsDataAccessInterface;

/**
 * Tests the transfer use case with a fake bench, so no real items or screen are needed.
 */
class TransferContentsInteractorTest {
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

  /** A pretend bench: a few items with an id, a type, and contents. */
  private static class FakeBench implements TransferContentsDataAccessInterface {
    private final Map<String, Mixture> mixtures = new HashMap<>();
    private final Map<String, Substance> substances = new HashMap<>();
    private final Map<String, ItemType> types = new HashMap<>();

    FakeBench() {
      Mixture conical = new Mixture();                     // after the first mix in Task A
      conical.add(Substances.ASPIRIN, 1.11);
      conical.add(Substances.VANILLIN, 0.3);
      conical.add(Substances.ETHER, 4);
      conical.add(Substances.WATER, 2);
      conical.mix(Mixture.FULL_MIX);
      mixtures.put("conical", conical);
      types.put("conical", ItemType.CONICAL_TUBE);

      mixtures.put("etherCylinder", new Mixture());
      types.put("etherCylinder", ItemType.GRADUATED_CYLINDER);

      substances.put("naohDropper", Substances.NAOH);
      substances.put("hclDropper", Substances.HCL);
      substances.put("etherBottle", Substances.ETHER);
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
  }

  @Test
  void tenDropsOfNaohMakesTheTubeBasic() {
    interactor.execute(new TransferContentsInputData("naohDropper", "conical", 10,
        TransferUnit.DROPS));
    assertNull(error);
    assertEquals(AcidBaseRule.BASIC_PH, output.getPh());
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
    interactor.execute(new TransferContentsInputData("etherBottle", "etherCylinder", 2,
        TransferUnit.ML));
    assertNull(error);
    assertEquals(List.of("ORGANIC"), output.getLayerPhases());
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
}
