package usecase.navigatestep;

import entity.Lab;
import entity.StepStatus;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Moves the student between steps. Next only works once the step is complete; Prev always
 * works, except on the first step.
 */
public class NavigateStepInteractor implements NavigateStepInputBoundary {

  private final LabDataAccessInterface dataAccess;
  private final NavigateStepOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab is kept
   * @param presenter receives the result
   */
  public NavigateStepInteractor(LabDataAccessInterface dataAccess,
      NavigateStepOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(NavigateStepInputData in) {
    Lab lab = dataAccess.getLab();
    if (in.isForward()) {
      if (lab.status() != StepStatus.COMPLETE) {
        presenter.prepareFailView("Finish this step first.");
        return;
      }
      if (!lab.advance()) {
        presenter.prepareFailView("That was the last step.");
        return;
      }
      dataAccess.saveLab(lab);
      // The bench as the new step starts, so Prev can come back to exactly this.
      dataAccess.markStepStart(lab.getCurrentIndex());
    } else {
      int previous = lab.getCurrentIndex() - 1;
      if (previous < 0) {
        presenter.prepareFailView("You are already on the first step.");
        return;
      }
      // Rewinding also puts the bench back as it was when the previous step started.
      if (!dataAccess.rewindToStepStart(previous)) {
        lab.goBack();
        dataAccess.saveLab(lab);
      }
      lab = dataAccess.getLab();
    }
    presenter.prepareSuccessView(new NavigateStepOutputData(LabStateMapper.from(lab)));
  }
}
