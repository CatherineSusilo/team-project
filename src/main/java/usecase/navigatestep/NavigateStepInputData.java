package usecase.navigatestep;

/**
 * Which way the student wants to go.
 */
public class NavigateStepInputData {

  private final boolean forward;

  /**
   * Creates the data.
   *
   * @param forward true for Next, false for Prev
   */
  public NavigateStepInputData(boolean forward) {
    this.forward = forward;
  }

  public boolean isForward() {
    return forward;
  }
}
