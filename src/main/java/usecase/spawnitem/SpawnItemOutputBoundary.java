package usecase.spawnitem;

/**
 * What the SpawnItem use case tells the presenter.
 */
public interface SpawnItemOutputBoundary {

  /**
   * Shows the lab with the new item on the bench.
   *
   * @param out the new state
   */
  void prepareSuccessView(SpawnItemOutputData out);

  /**
   * Shows why the item couldn't be put on the bench.
   *
   * @param error the reason
   */
  void prepareFailView(String error);
}
