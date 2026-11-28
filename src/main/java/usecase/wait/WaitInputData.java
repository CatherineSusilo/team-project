package usecase.wait;

/**
 * How long the student waited: sent once, when the timer bar finishes or Skip is pressed.
 */
public final class WaitInputData {
  private final double minutes;

  /**
   * Creates the input.
   *
   * @param minutes how many lab minutes passed, e.g. 1 for the layers to settle
   */
  public WaitInputData(double minutes) {
    this.minutes = minutes;
  }

  public double getMinutes() {
    return minutes;
  }
}
