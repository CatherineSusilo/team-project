package usecase.navigatestep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.Lab;
import entity.UnknownSample;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.LabDataAccessInterface;
import usecase.LabStateData;

class NavigateStepInteractorTest {

  private static final class FakeDataAccess implements LabDataAccessInterface {
    private final Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    private int saves;
    private final List<Integer> marked = new ArrayList<>();
    private boolean canRewind;
    private int rewoundTo = -1;

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
      marked.add(index);
    }

    @Override
    public boolean rewindToStepStart(int index) {
      rewoundTo = index;
      return canRewind;
    }
  }

  private static final class RecordingPresenter implements NavigateStepOutputBoundary {
    private LabStateData state;
    private String error;

    @Override
    public void prepareSuccessView(NavigateStepOutputData out) {
      state = out.getState();
    }

    @Override
    public void prepareFailView(String message) {
      error = message;
    }
  }

  @Test
  void nextWorksOnceTheStepIsCompleteAndSavesTheLab() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();

    new NavigateStepInteractor(dataAccess, presenter).execute(new NavigateStepInputData(true));

    assertNull(presenter.error);
    assertEquals(1, presenter.state.getStepIndex());
    assertEquals(1, dataAccess.saves);
    assertTrue(presenter.state.isNextLocked());
    assertEquals(false, presenter.state.isPrevLocked());
  }

  @Test
  void nextIsRefusedWhileTheStepIsIncomplete() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    dataAccess.getLab().advance();
    RecordingPresenter presenter = new RecordingPresenter();

    new NavigateStepInteractor(dataAccess, presenter).execute(new NavigateStepInputData(true));

    assertEquals("Finish this step first.", presenter.error);
    assertEquals(1, dataAccess.getLab().getCurrentIndex());
    assertEquals(0, dataAccess.saves);
  }

  @Test
  void prevAlwaysWorksExceptOnTheFirstStep() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    RecordingPresenter presenter = new RecordingPresenter();
    NavigateStepInteractor interactor = new NavigateStepInteractor(dataAccess, presenter);

    interactor.execute(new NavigateStepInputData(false));
    assertEquals("You are already on the first step.", presenter.error);

    dataAccess.getLab().advance();
    interactor.execute(new NavigateStepInputData(false));
    assertEquals(0, presenter.state.getStepIndex());
  }

  @Test
  void nextMarksWhereTheNewStepStarts() {
    FakeDataAccess dataAccess = new FakeDataAccess();

    new NavigateStepInteractor(dataAccess, new RecordingPresenter())
        .execute(new NavigateStepInputData(true));

    assertEquals(List.of(1), dataAccess.marked);
  }

  @Test
  void prevRewindsTheBenchToTheStartOfThePreviousStep() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    dataAccess.canRewind = true;
    dataAccess.getLab().advance();
    RecordingPresenter presenter = new RecordingPresenter();

    new NavigateStepInteractor(dataAccess, presenter).execute(new NavigateStepInputData(false));

    assertEquals(0, dataAccess.rewoundTo);
    assertEquals(0, dataAccess.saves);
    assertEquals(1, dataAccess.getLab().getCurrentIndex());
  }

  @Test
  void prevJustStepsBackWhenThereIsNothingToRewindTo() {
    FakeDataAccess dataAccess = new FakeDataAccess();
    dataAccess.getLab().advance();
    RecordingPresenter presenter = new RecordingPresenter();

    new NavigateStepInteractor(dataAccess, presenter).execute(new NavigateStepInputData(false));

    assertEquals(0, presenter.state.getStepIndex());
    assertEquals(1, dataAccess.saves);
  }
}
