package usecase.spawnitem;

import usecase.LabStateData;

/**
 * The lab after an item was put on the bench.
 */
public class SpawnItemOutputData {

  private final LabStateData state;

  /**
   * Creates the data.
   *
   * @param state how the lab looks now
   */
  public SpawnItemOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
