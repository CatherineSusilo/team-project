package usecase.navigatestep;

/**
 * What the controller asks the NavigateStep use case to do.
 */
public interface NavigateStepInputBoundary {

  /**
   * Moves to the next or the previous step.
   *
   * @param in which way to go
   */
  void execute(NavigateStepInputData in);
}
