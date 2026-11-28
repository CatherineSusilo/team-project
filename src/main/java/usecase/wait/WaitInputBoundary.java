package usecase.wait;

/**
 * Lets time pass on the bench.
 */
public interface WaitInputBoundary {
  /**
   * Lets the given lab minutes pass.
   *
   * @param in how long the student waited
   */
  void execute(WaitInputData in);
}
