package usecase.wait;

import entity.Lab;
import java.io.UncheckedIOException;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Lets time pass on the bench (A8, A9: the layers settle). Only works while the current step
 * is waiting for something, i.e. while the timer bar shows. A wait isn't a step action, so it
 * isn't recorded, but it can be undone.
 */
public class WaitInteractor implements WaitInputBoundary {
  private final LabDataAccessInterface dataAccess;
  private final WaitOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab (and its bench) is kept
   * @param presenter receives the result
   */
  public WaitInteractor(LabDataAccessInterface dataAccess, WaitOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(WaitInputData in) {
    Lab lab = dataAccess.getLab();
    if (in.getMinutes() <= 0) {
      presenter.prepareFailView("Choose a time above 0.");
      return;
    }
    // e.g. nothing has been mixed yet, so there's nothing to settle
    if (lab.currentStep().waitMinutes(lab) == 0) {
      presenter.prepareFailView("There's nothing to wait for yet.");
      return;
    }
    dataAccess.pushSnapshot();
    lab.advanceTime(in.getMinutes());           // the mixed tube settles into two layers
    presenter.prepareSuccessView(new WaitOutputData(LabStateMapper.from(lab)));
    try {
      dataAccess.saveLab(lab);
    } catch (UncheckedIOException e) {
      // the wait already happened; only writing the save file failed, so don't redo it
      presenter.prepareFailView("Done, but your progress couldn't be saved.");
    }
  }
}
