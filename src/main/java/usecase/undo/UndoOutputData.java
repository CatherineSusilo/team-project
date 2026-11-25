package usecase.undo;

import usecase.LabStateData;

/**
 * The lab after the student's last action was undone.
 */
public class UndoOutputData {

  private final LabStateData state;

  /**
   * Creates the data.
   *
   * @param state how the lab looks now
   */
  public UndoOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
