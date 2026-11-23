package usecase.useitem;

/**
 * Does something to one item on the bench (label it, cover it, ...).
 */
public interface UseItemInputBoundary {

  /**
   * Executes the use item use case.
   *
   * @param input what the student did
   */
  void execute(UseItemInputData input);
}
