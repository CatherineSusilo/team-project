package usecase.undo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import entity.Action;
import entity.ActionKind;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.StepStatus;
import entity.Vec2;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;
import usecase.LabStateData;

class UndoInteractorTest {

  private static final class RecordingPresenter implements UndoOutputBoundary {
    private LabStateData state;
    private String error;

    @Override
    public void prepareSuccessView(UndoOutputData out) {
      state = out.getState();
    }

    @Override
    public void prepareFailView(String message) {
      error = message;
    }
  }

  private final InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject();
  private final RecordingPresenter presenter = new RecordingPresenter();

  @Test
  void withNothingToUndoItSaysSoAndChangesNothing() {
    new UndoInteractor(dataAccess, presenter).execute(new UndoInputData());

    assertEquals("Nothing to undo.", presenter.error);
    assertNull(presenter.state);
  }

  @Test
  void undoPutsTheBenchBackAndShowsTheRestoredLab() {
    Lab lab = dataAccess.getLab();
    int before = lab.getBench().getItems().size();
    dataAccess.pushSnapshot();
    lab.getBench().addItem(new ItemFactory().create(
        ItemTemplates.find("conical_tube").orElseThrow(), "conical_tube-1"), new Vec2(5, 5));
    assertEquals(before + 1, lab.getBench().getItems().size());

    new UndoInteractor(dataAccess, presenter).execute(new UndoInputData());

    assertNull(presenter.error);
    assertEquals(before, presenter.state.getItems().size());
    assertEquals(before, dataAccess.getLab().getBench().getItems().size());
  }

  @Test
  void undoingAMistakeClearsItFromTheShownState() {
    dataAccess.pushSnapshot();
    Lab lab = dataAccess.getLab();
    lab.record(new Action(ActionKind.MIX, "nothing-1", "nothing-2", 1, null, ""));
    assertEquals(StepStatus.MISTAKE, lab.status());

    new UndoInteractor(dataAccess, presenter).execute(new UndoInputData());

    assertTrue(presenter.state.getStatus() != StepStatus.MISTAKE);
  }

  @Test
  void aFailedSaveStillShowsTheRestoredLabAndWarnsTheStudent() {
    InMemoryLabDataAccessObject failingSave = new InMemoryLabDataAccessObject() {
      @Override
      public boolean popSnapshot() {
        super.popSnapshot();
        throw new UncheckedIOException(new IOException("disk full"));
      }
    };
    Lab lab = failingSave.getLab();
    int before = lab.getBench().getItems().size();
    failingSave.pushSnapshot();
    lab.getBench().addItem(new ItemFactory().create(
        ItemTemplates.find("conical_tube").orElseThrow(), "conical_tube-1"), new Vec2(5, 5));

    new UndoInteractor(failingSave, presenter).execute(new UndoInputData());

    assertEquals(before, presenter.state.getItems().size());
    assertEquals("Undone, but your progress couldn't be saved.", presenter.error);
  }
}
