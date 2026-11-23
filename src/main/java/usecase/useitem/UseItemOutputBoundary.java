package usecase.useitem;

/**
 * Receives the result of using an item.
 */
public interface UseItemOutputBoundary {

  /**
   * Shows the item after it was used.
   *
   * @param output what the item looks like now
   */
  void prepareSuccessView(UseItemOutputData output);

  /**
   * Shows why it didn't work. Nothing on the bench changed.
   *
   * @param error what went wrong, in plain words
   */
  void prepareFailView(String error);
}
