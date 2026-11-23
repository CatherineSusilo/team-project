package usecase.useitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import entity.ItemType;
import entity.Operation;
import entity.Vec2;
import entity.Vessel;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import usecase.UseItemDataAccessInterface;

/**
 * Tests the use item use case with a fake bench: labelling the tubes (A2).
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

  /** A pretend bench with a conical tube and a test tube. */
  private static class FakeBench implements UseItemDataAccessInterface {
    private final Map<String, Vessel> vessels = new HashMap<>();

    FakeBench() {
      vessels.put("conical", new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6));
      vessels.put("testTube", new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8));
    }

    @Override
    public Vessel getVessel(String itemId) {
      return vessels.get(itemId);
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
  void operationsNotBuiltYetFail() {
    interactor.execute(new UseItemInputData("conical", Operation.COVER, ""));
    assertEquals("That doesn't work yet.", error);
  }
}
