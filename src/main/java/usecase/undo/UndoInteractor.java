package usecase.undo;

import java.io.UncheckedIOException;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Steps the student back one action in the current step. The data layer keeps a snapshot of the
 * lab from before each action; undoing puts the latest one back, so the bench, any standing
 * mistake and the step's progress all return to how they were.
 */
public class UndoInteractor implements UndoInputBoundary {
  private final LabDataAccessInterface dataAccess;
  private final UndoOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab and its snapshots are kept
   * @param presenter receives the result
   */
  public UndoInteractor(LabDataAccessInterface dataAccess, UndoOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(UndoInputData in) {
    boolean undone;
    try {
      undone = dataAccess.popSnapshot();
    } catch (UncheckedIOException e) {
      // The undo already happened in memory; only writing the save file failed.
      showRestoredLab();
      presenter.prepareFailView("Undone, but your progress couldn't be saved.");
      return;
    }
    if (!undone) {
      presenter.prepareFailView("Nothing to undo.");
      return;
    }
    showRestoredLab();
  }

  // popSnapshot swapped in the restored lab, so read it again.
  private void showRestoredLab() {
    presenter.prepareSuccessView(new UndoOutputData(LabStateMapper.from(dataAccess.getLab())));
  }
}
