package adapter.controller;

import entity.Operation;
import usecase.useitem.UseItemInputBoundary;
import usecase.useitem.UseItemInputData;

/**
 * Passes a click on an item (label it, cover it, ...) on to be carried out.
 */
public class UseItemController {
  private final UseItemInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor carries out the action
   */
  public UseItemController(UseItemInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Carries out the action.
   *
   * @param itemId the item that was clicked, e.g. "conical-1"
   * @param operation "LABEL" or "COVER" (plain text, so the view never imports entity)
   * @param text the label text for LABEL, otherwise ""
   */
  public void execute(String itemId, String operation, String text) {
    interactor.execute(new UseItemInputData(itemId, Operation.valueOf(operation), text));
  }
}
