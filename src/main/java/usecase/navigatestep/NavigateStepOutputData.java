package usecase.navigatestep;

import usecase.LabStateData;

/**
 * The lab after the student changed step.
 */
public class NavigateStepOutputData {

  private final LabStateData state;

  /**
   * Creates the data.
   *
   * @param state how the lab looks now
   */
  public NavigateStepOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
