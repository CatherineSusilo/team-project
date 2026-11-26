package usecase.transfercontents;

import usecase.LabStateData;

/**
 * The lab after the transfer, so the screen can redraw the containers (and the step).
 */
public final class TransferContentsOutputData {
  private final LabStateData state;

  /**
   * Creates the output.
   *
   * @param state the whole lab after the change: every item, the step, Next locked or not
   */
  public TransferContentsOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
