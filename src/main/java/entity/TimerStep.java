package entity;

import java.util.List;

/**
 * A wait in a step: the tube was just mixed, so the student waits for its layers to separate
 * (A8, A9: 1 minute). While the tube is cloudy the step asks for a wait, so the timer bar shows.
 * Waiting itself is not an action, so this part expects none.
 */
public class TimerStep extends LabStep {
  private final ItemType tubeType;
  private final double minutes;

  /**
   * Creates the part. It sits inside a {@link MultiPartStep}, which has the step's title, text
   * and tray, so the part has none of its own.
   *
   * @param tubeType the kind of tube that has to settle, e.g. the conical tube
   * @param minutes how long the wait is, in lab minutes
   */
  public TimerStep(ItemType tubeType, double minutes) {
    super(0, "", "", "", List.of(), false);
    this.tubeType = tubeType;
    this.minutes = minutes;
  }

  public double getMinutes() {
    return minutes;
  }

  @Override
  public boolean isComplete(Lab lab) {
    Vessel tube = tube(lab);
    return tube != null && tube.getMixture().isSettled();
  }

  @Override
  public boolean expects(Action action) {
    return false;                     // waiting isn't recorded as an action
  }

  @Override
  public double waitMinutes(Lab lab) {
    // the timer bar shows while the tube is still cloudy from mixing
    if (isComplete(lab) || tube(lab) == null) {
      return 0;
    }
    return minutes;
  }

  // Helper Function
  // The first tube of that kind on the bench, or null.
  private Vessel tube(Lab lab) {
    for (LabItem item : lab.getBench().getItems()) {
      if (item.getType() == tubeType) {
        return (Vessel) item;
      }
    }
    return null;
  }
}
