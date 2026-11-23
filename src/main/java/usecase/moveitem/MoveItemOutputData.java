package usecase.moveitem;

import usecase.LabStateData;

/**
 * The lab after an item was moved.
 */
public class MoveItemOutputData {

  private final LabStateData state;

  /**
   * Creates the data.
   *
   * @param state how the lab looks now
   */
  public MoveItemOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
