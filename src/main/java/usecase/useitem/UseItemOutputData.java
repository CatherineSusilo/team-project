package usecase.useitem;

import usecase.LabStateData;

/**
 * The lab after an item was used, so the screen can redraw it (its label tape, its cover, the
 * scale's number).
 */
public final class UseItemOutputData {
  private final LabStateData state;

  /**
   * Creates the output.
   *
   * @param state the whole lab after the change: every item, the step, Next locked or not
   */
  public UseItemOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
