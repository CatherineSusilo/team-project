package usecase.moveitem;

/**
 * What the controller asks the MoveItem use case to do.
 */
public interface MoveItemInputBoundary {

  /**
   * Moves an item on the bench.
   *
   * @param in which item and where to
   */
  void execute(MoveItemInputData in);
}
