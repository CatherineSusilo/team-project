package usecase.useitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.Bench;
import entity.BuchnerFunnel;
import entity.ItemType;
import entity.Lab;
import entity.Operation;
import entity.Scale;
import entity.UnknownSample;
import entity.VacuumTap;
import entity.Vec2;
import entity.Vessel;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;
import usecase.ItemData;
import usecase.LabDataAccessInterface;

/**
 * Tests the use item use case on a real lab: labelling (A2, A3), covering (A3) and taring
 * the scale (A4).
 */
class UseItemInteractorTest {
  private final FakeLab bench = new FakeLab();
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

  /** A lab whose bench has tubes, the ether cylinder, a beaker holding the tube, and the scale. */
  private static class FakeLab implements LabDataAccessInterface {
    private final Lab lab = new Lab(Lab3Definition.steps(), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    private final Scale scale = new Scale("scale", new Vec2(0, 0));
    private int snapshots;
    private int saves;
    private boolean diskFull;                              // saving to the file fails

    FakeLab() {
      addVessel(new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6));
      addVessel(new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8));
      addVessel(new Vessel("etherCylinder", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0), 10,
          20));
      Vessel beaker = new Vessel("beaker", ItemType.BEAKER, new Vec2(0, 0), 50, 30);
      addVessel(beaker);
      lab.getBench().addItem(scale, new Vec2(0, 0));
      beaker.hold("conical");                              // A4: tube in the beaker
      scale.place("beaker");                               // beaker on the scale
    }

    private void addVessel(Vessel vessel) {
      lab.getBench().addItem(vessel, new Vec2(0, 0));
    }

    Vessel getVessel(String id) {
      return (Vessel) lab.getBench().getItem(id);
    }

    Bench getBench() {
      return lab.getBench();
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

  /** How the screen shows one item after the last successful use. */
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
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "A"));
    assertEquals("Done, but your progress couldn't be saved.", error);
    assertNotNull(output);                                  // the change is still shown
  }

  @Test
  void usingAnItemSavesAnUndoPointAndTheLab() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "A"));
    assertEquals(1, bench.snapshots);                    // Undo can go back to before it
    assertEquals(1, bench.saves);
  }

  @Test
  void aRejectedUseChangesNothing() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "   "));
    assertEquals(0, bench.snapshots);
    assertEquals(0, bench.saves);
  }

  @Test
  void labelsTheConicalTube() {
    interactor.execute(new UseItemInputData("conical", Operation.LABEL, "A"));
    assertNull(error);
    assertEquals("A", bench.getVessel("conical").getLabel());
    assertEquals("A", shown("conical").getLabel());       // the label tape on screen
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
    assertTrue(shown("etherCylinder").isCovered());     // the ether-covered PNG
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
    assertEquals("0.000", shown("scale").getDisplay());
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

  // ----- B3: filter paper and the vacuum tap -----

  @Test
  void addPaperPutsPaperInTheFunnel() {
    BuchnerFunnel funnel = new BuchnerFunnel("funnel", new Vec2(0, 0), 10, 60);
    bench.getBench().addItem(funnel, new Vec2(0, 0));
    interactor.execute(new UseItemInputData("funnel", Operation.ADD_PAPER, ""));
    assertNull(error);
    assertTrue(funnel.hasPaper());
  }

  @Test
  void paperOnlyGoesInTheFunnel() {
    interactor.execute(new UseItemInputData("conical", Operation.ADD_PAPER, ""));
    assertEquals("Put the paper in the Büchner funnel.", error);
  }

  @Test
  void vacuumTurnsOnAndOff() {
    VacuumTap tap = new VacuumTap("tap", new Vec2(0, 0));
    bench.getBench().addItem(tap, new Vec2(0, 0));
    interactor.execute(new UseItemInputData("tap", Operation.VACUUM_ON, ""));
    assertTrue(tap.isOn());
    interactor.execute(new UseItemInputData("tap", Operation.VACUUM_OFF, ""));
    assertFalse(tap.isOn());
    assertEquals(2, bench.snapshots);                      // each can be undone
  }

  @Test
  void onlyTheTapTurnsTheVacuumOn() {
    interactor.execute(new UseItemInputData("conical", Operation.VACUUM_ON, ""));
    assertEquals("Use the vacuum tap.", error);
  }
}
