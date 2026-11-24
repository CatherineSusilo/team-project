package usecase.useitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ApiType;
import entity.Bench;
import entity.ItemType;
import entity.Operation;
import entity.Scale;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import usecase.UseItemDataAccessInterface;

/**
 * Tests the use item use case with a fake bench: labelling (A2, A3), covering (A3) and
 * taring the scale (A4).
 */
class UseItemInteractorTest {
  private final FakeBench bench = new FakeBench();
  private UseItemOutputData output;
  private String error;
  private final UseItemInteractor interactor = new UseItemInteractor(bench,
      new UseItemOutputBoundary() {
        @Override
        public void prepareSuccessView(UseItemOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  /** A pretend bench: tubes, the ether cylinder, a beaker holding the tube, and the scale. */
  private static class FakeBench implements UseItemDataAccessInterface {
    private final Map<String, Vessel> vessels = new HashMap<>();
    private final Bench realBench = new Bench();
    private final Scale scale = new Scale("scale", new Vec2(0, 0));

    FakeBench() {
      vessels.put("conical", new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6));
      vessels.put("testTube", new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8));
      vessels.put("etherCylinder",
          new Vessel("etherCylinder", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0), 10, 20));
      vessels.put("beaker", new Vessel("beaker", ItemType.BEAKER, new Vec2(0, 0), 50, 30));
      for (Vessel vessel : vessels.values()) {
        realBench.addItem(vessel, new Vec2(0, 0));
      }
      realBench.addItem(scale, new Vec2(0, 0));
      vessels.get("beaker").hold("conical");               // A4: tube in the beaker
      scale.place("beaker");                               // beaker on the scale
    }

    @Override
    public Vessel getVessel(String itemId) {
      return vessels.get(itemId);
    }

    @Override
    public Scale getScale(String itemId) {
      if (itemId.equals("scale")) {
        return scale;
      }
      return null;
    }

    @Override
    public Bench getBench() {
      return realBench;
    }
  }

  @Test
  void labelsTheConicalTube() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "A"));
    assertNull(error);
    assertEquals("A", bench.getVessel("conical").getLabel());
    assertEquals("A", output.getLabel());
    assertEquals("conical", output.getItemId());
  }

  @Test
  void labelsTheTestTube() {
    interactor.execute(new UseItemInputData("testTube", Operation.LABEL, "aqueous"));
    assertNull(error);
    assertEquals("aqueous", bench.getVessel("testTube").getLabel());
  }

  @Test
  void trimsSpaces() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "  A "));
    assertEquals("A", bench.getVessel("conical").getLabel());
  }

  @Test
  void relabelReplacesTheOldLabel() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "B"));
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "A"));
    assertEquals("A", bench.getVessel("conical").getLabel());
  }

  @Test
  void wrongLabelIsStillStored() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "banana"));
    assertNull(error);                                  // the A2 step decides if it's right
    assertEquals("banana", bench.getVessel("conical").getLabel());
  }

  @Test
  void blankLabelFails() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "   "));
    assertEquals("Write something on the label.", error);
    assertEquals("", bench.getVessel("conical").getLabel());
  }

  @Test
  void labelOnNothingFails() {
    interactor.execute(new UseItemInputData("bench", Operation.LABEL, "A"));
    assertEquals("Pick a container.", error);
  }

  @Test
  void labelsTheEtherCylinder() {
    interactor.execute(new UseItemInputData("etherCylinder", Operation.LABEL, "ether"));
    assertEquals("ether", bench.getVessel("etherCylinder").getLabel());    // A3
  }

  @Test
  void coversTheEtherCylinder() {
    interactor.execute(new UseItemInputData("etherCylinder", Operation.COVER, ""));
    assertNull(error);
    assertTrue(bench.getVessel("etherCylinder").isCovered());
    assertTrue(output.isCovered());
    assertFalse(bench.getVessel("conical").isCovered());
  }

  @Test
  void coverTwiceStaysCovered() {
    interactor.execute(new UseItemInputData("etherCylinder", Operation.COVER, ""));
    interactor.execute(new UseItemInputData("etherCylinder", Operation.COVER, ""));
    assertNull(error);
    assertTrue(bench.getVessel("etherCylinder").isCovered());
  }

  @Test
  void coverOnNothingFails() {
    interactor.execute(new UseItemInputData("bench", Operation.COVER, ""));
    assertEquals("Pick a container.", error);
  }

  @Test
  void tareZeroesTheScale() {
    interactor.execute(new UseItemInputData("scale", Operation.TARE, ""));
    assertNull(error);
    assertEquals("0.000", output.getDisplay());
  }

  @Test
  void scoopAfterTareShowsOnTheScale() {
    interactor.execute(new UseItemInputData("scale", Operation.TARE, ""));
    new UnknownSample('A', ApiType.ASPIRIN).addTo(bench.getVessel("conical").getMixture(), 0.200);
    assertEquals("0.200", bench.scale.display(bench.getBench()));
  }

  @Test
  void tareOnAVesselFails() {
    interactor.execute(new UseItemInputData("conical", Operation.TARE, ""));
    assertEquals("Use the scale's Tare button.", error);
  }

  @Test
  void operationsNotBuiltYetFail() {
    interactor.execute(new UseItemInputData("conical", Operation.STIR, ""));
    assertEquals("That doesn't work yet.", error);
  }
}
