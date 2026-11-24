package usecase.moveitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.Scale;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import org.junit.jupiter.api.Test;
import usecase.LabDataAccessInterface;
import usecase.LabStateData;

class MoveItemInteractorTest {

  private static final class FakeDataAccess implements LabDataAccessInterface {
    private final Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    private int saves;

    @Override
    public Lab getLab() {
      return lab;
    }

    @Override
    public void saveLab(Lab saved) {
      saves++;
    }

    @Override
    public void pushSnapshot() {
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

  private static final class RecordingPresenter implements MoveItemOutputBoundary {
    private LabStateData state;
    private String error;

    @Override
    public void prepareSuccessView(MoveItemOutputData out) {
      state = out.getState();
    }

    @Override
    public void prepareFailView(String message) {
      error = message;
    }
  }

  @Test
  void movesAnItemToWhereItWasLetGoAndSaves() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();

    new MoveItemInteractor(dataAccess, presenter)
        .execute(new MoveItemInputData("unknown_vial-1", 400, 250));

    assertNull(presenter.error);
    assertEquals(400, dataAccess.getLab().getBench().getItem("unknown_vial-1")
        .getPosition().getX());
    assertEquals(250, presenter.state.getItems().get(0).getY());
    assertEquals(1, dataAccess.saves);
  }

  @Test
  void refusesAnItemThatIsNotOnTheBench() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();

    new MoveItemInteractor(dataAccess, presenter)
        .execute(new MoveItemInputData("ghost-1", 1, 1));

    assertEquals("That item isn't on the bench.", presenter.error);
    assertEquals(0, dataAccess.saves);
  }

  private static void put(Lab lab, String templateId, double x, double y) {
    String id = lab.getBench().nextId(templateId);
    lab.getBench().addItem(new ItemFactory().create(ItemTemplates.find(templateId).orElseThrow(),
        id), new Vec2(x, y));
  }

  private static void move(FakeDataAccess dataAccess, String id, double x, double y) {
    new MoveItemInteractor(dataAccess, new RecordingPresenter())
        .execute(new MoveItemInputData(id, x, y));
  }

  @Test
  void aTubeDroppedByTheBeakerGoesInItAndTheScaleAddsBothWeights() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    Lab lab = dataAccess.getLab();
    put(lab, "beaker", 500, 500);
    put(lab, "conical_tube", 100, 100);
    put(lab, "scale", 700, 500);
    Scale scale = (Scale) lab.getBench().getItem("scale-1");

    move(dataAccess, "beaker-1", 690, 500);
    assertEquals("30.000", scale.display(lab.getBench()));

    move(dataAccess, "conical_tube-1", 670, 500);
    assertEquals("36.000", scale.display(lab.getBench()));

    scale.tare(lab.getBench());
    assertEquals("0.000", scale.display(lab.getBench()));
  }

  @Test
  void movingAThingAwayTakesItOffTheScale() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    Lab lab = dataAccess.getLab();
    put(lab, "beaker", 500, 500);
    put(lab, "scale", 700, 500);
    Scale scale = (Scale) lab.getBench().getItem("scale-1");

    move(dataAccess, "beaker-1", 690, 500);
    assertEquals("beaker-1", scale.getPlacedId());

    move(dataAccess, "beaker-1", 100, 100);
    assertNull(scale.getPlacedId());
    assertEquals("0.000", scale.display(lab.getBench()));
  }

  @Test
  void movingATubeOutOfTheBeakerLetsGoOfIt() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    Lab lab = dataAccess.getLab();
    put(lab, "beaker", 500, 500);
    put(lab, "conical_tube", 100, 100);

    move(dataAccess, "conical_tube-1", 490, 500);
    assertEquals("conical_tube-1",
        ((Vessel) lab.getBench().getItem("beaker-1")).getHoldingId());

    move(dataAccess, "conical_tube-1", 100, 100);
    assertNull(((Vessel) lab.getBench().getItem("beaker-1")).getHoldingId());
    assertTrue(lab.getBench().getItem("conical_tube-1") != null);
  }
}
