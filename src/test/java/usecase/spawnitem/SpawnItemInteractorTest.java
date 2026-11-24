package usecase.spawnitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import data.Lab3Definition;
import entity.ApiType;
import entity.ItemFactory;
import entity.Lab;
import entity.UnknownSample;
import org.junit.jupiter.api.Test;
import usecase.LabDataAccessInterface;
import usecase.LabStateData;

class SpawnItemInteractorTest {

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

  private static final class RecordingPresenter implements SpawnItemOutputBoundary {
    private LabStateData state;
    private String error;

    @Override
    public void prepareSuccessView(SpawnItemOutputData out) {
      state = out.getState();
    }

    @Override
    public void prepareFailView(String message) {
      error = message;
    }
  }

  private static FakeDataAccess onSecondStep() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    dataAccess.getLab().advance();
    return dataAccess;
  }

  @Test
  void spawnsATrayItemWhereItWasDroppedAndSaves() {
    FakeDataAccess dataAccess = onSecondStep();
    RecordingPresenter presenter = new RecordingPresenter();

    new SpawnItemInteractor(dataAccess, presenter, new ItemFactory())
        .execute(new SpawnItemInputData("conical_tube", 200, 150));

    assertNull(presenter.error);
    assertEquals(1, dataAccess.getLab().getBench().countOf("conical_tube"));
    assertEquals(200, dataAccess.getLab().getBench().getItem("conical_tube-1")
        .getPosition().getX());
    assertEquals(1, dataAccess.saves);
    assertEquals(2, presenter.state.getItems().size());
  }

  @Test
  void spawningBothTubesCompletesTheStepAndUnlocksNext() {
    FakeDataAccess dataAccess = onSecondStep();
    RecordingPresenter presenter = new RecordingPresenter();
    SpawnItemInteractor interactor =
        new SpawnItemInteractor(dataAccess, presenter, new ItemFactory());

    interactor.execute(new SpawnItemInputData("conical_tube", 200, 150));
    assertEquals(true, presenter.state.isNextLocked());
    interactor.execute(new SpawnItemInputData("test_tube", 300, 150));

    assertEquals(false, presenter.state.isNextLocked());
    assertEquals("COMPLETE", presenter.state.getStatus().name());
  }

  @Test
  void refusesAnItemTheCurrentStepDoesNotOffer() {
    FakeDataAccess dataAccess = onSecondStep();
    RecordingPresenter presenter = new RecordingPresenter();

    new SpawnItemInteractor(dataAccess, presenter, new ItemFactory())
        .execute(new SpawnItemInputData("scale", 10, 10));

    assertEquals("That isn't needed for this step.", presenter.error);
    assertEquals(0, dataAccess.getLab().getBench().countOf("scale"));
    assertEquals(0, dataAccess.saves);
  }

  @Test
  void refusesAnUnknownTemplate() {
    FakeDataAccess dataAccess = onSecondStep();
    RecordingPresenter presenter = new RecordingPresenter();

    new SpawnItemInteractor(dataAccess, presenter, new ItemFactory())
        .execute(new SpawnItemInputData("flux_capacitor", 10, 10));

    assertEquals("That isn't needed for this step.", presenter.error);
  }
}
