package usecase.spawnitem;

/**
 * What the controller asks the SpawnItem use case to do.
 */
public interface SpawnItemInputBoundary {

  /**
   * Puts a new item from the tray on the bench.
   *
   * @param in what to spawn and where
   */
  void execute(SpawnItemInputData in);
}
