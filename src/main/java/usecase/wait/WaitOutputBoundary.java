package usecase.wait;

/**
 * What the Wait use case tells the presenter.
 */
public interface WaitOutputBoundary {
  /**
   * Shows the lab after the wait.
   *
   * @param out the new state
   */
  void prepareSuccessView(WaitOutputData out);

  /**
   * Shows why the wait didn't happen.
   *
   * @param error the reason
   */
  void prepareFailView(String error);
}
