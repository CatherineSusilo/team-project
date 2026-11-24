package usecase.moveitem;

/**
 * What the MoveItem use case tells the presenter.
 */
public interface MoveItemOutputBoundary {

  /**
   * Shows the lab with the item in its new place.
   *
   * @param out the new state
   */
  void prepareSuccessView(MoveItemOutputData out);

  /**
   * Shows why the item couldn't be moved.
   *
   * @param error the reason
   */
  void prepareFailView(String error);
}
