package adapter.controller;

import usecase.spawnitem.SpawnItemInputBoundary;
import usecase.spawnitem.SpawnItemInputData;

/**
 * Passes the student's request to the SpawnItem use case.
 */
public class SpawnItemController {

  private final SpawnItemInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor the use case to call
   */
  public SpawnItemController(SpawnItemInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Puts a tray item on the bench.
   *
   * @param templateId the tray item's template id
   * @param x where it was dropped, horizontally
   * @param y where it was dropped, vertically
   */
  public void execute(String templateId, double x, double y) {
    interactor.execute(new SpawnItemInputData(templateId, x, y));
  }
}
