package usecase.moveitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import data.Lab3Definition;
import entity.Lab;
import org.junit.jupiter.api.Test;
import usecase.LabDataAccessInterface;
import usecase.LabStateData;

class MoveItemInteractorTest {

  private static final class FakeDataAccess implements LabDataAccessInterface {
    private final Lab lab = Lab3Definition.create();
    private int saves;

    @Override
    public Lab getLab() {
      return lab;
    }

    @Override
    public void saveLab(Lab saved) {
      saves++;
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
}
