package usecase.undo;

/**
 * What the Undo use case tells the presenter.
 */
public interface UndoOutputBoundary {

  /**
   * Shows the lab after an action was undone.
   *
   * @param out the new state
   */
  void prepareSuccessView(UndoOutputData out);

  /**
   * Shows why nothing could be undone.
   *
   * @param error the reason
   */
  void prepareFailView(String error);
}
