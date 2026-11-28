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
    execute(itemId, operation, null, text);
  }

  /**
   * Carries out an action that also needs a second item.
   *
   * @param itemId the item that was clicked, e.g. "melting_point_app-1"
   * @param operation e.g. "START" (plain text, so the view never imports entity)
   * @param targetId the other item, e.g. the funnel the sample comes from (START)
   * @param text the label text for LABEL, otherwise ""
   */
  public void execute(String itemId, String operation, String targetId, String text) {
    interactor.execute(new UseItemInputData(itemId, Operation.valueOf(operation), targetId,
        text));
  }
}
