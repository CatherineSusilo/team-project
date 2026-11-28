package usecase.wait;

import usecase.LabStateData;

/**
 * The lab after the wait, so the screen can redraw it (e.g. two clear layers again).
 */
public final class WaitOutputData {
  private final LabStateData state;

  /**
   * Creates the output.
   *
   * @param state the whole lab after the wait: every item, the step, Next locked or not
   */
  public WaitOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
