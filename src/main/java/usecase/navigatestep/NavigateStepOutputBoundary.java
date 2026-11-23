package usecase.navigatestep;

/**
 * What the NavigateStep use case tells the presenter.
 */
public interface NavigateStepOutputBoundary {

  /**
   * Shows the lab after the student changed step.
   *
   * @param out the new state
   */
  void prepareSuccessView(NavigateStepOutputData out);

  /**
   * Shows why the student couldn't change step.
   *
   * @param error the reason
   */
  void prepareFailView(String error);
}
