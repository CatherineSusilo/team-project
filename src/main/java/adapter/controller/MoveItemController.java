package adapter.controller;

import usecase.moveitem.MoveItemInputBoundary;
import usecase.moveitem.MoveItemInputData;

/**
 * Passes the student's request to the MoveItem use case.
 */
public class MoveItemController {

  private final MoveItemInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor the use case to call
   */
  public MoveItemController(MoveItemInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Moves an item on the bench.
   *
   * @param itemId the item's id
   * @param x where it was let go, horizontally
   * @param y where it was let go, vertically
   */
  public void execute(String itemId, double x, double y) {
    interactor.execute(new MoveItemInputData(itemId, x, y));
  }
}
