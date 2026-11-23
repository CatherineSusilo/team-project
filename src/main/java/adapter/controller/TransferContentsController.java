package adapter.controller;

import usecase.transfercontents.TransferContentsInputBoundary;
import usecase.transfercontents.TransferContentsInputData;
import usecase.transfercontents.TransferUnit;

/**
 * Passes a drag-and-drop transfer (or a mix) on to be carried out.
 */
public class TransferContentsController {
  private final TransferContentsInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor carries out the transfer
   */
  public TransferContentsController(TransferContentsInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Carries out the transfer.
   *
   * @param sourceId what was dragged, e.g. the NaOH dropper bottle
   * @param targetId what it was dropped on, e.g. the conical tube
   * @param amount how much, in the given unit
   * @param unit mL, drops, grams, or pipette pumps for mixing
   */
  public void execute(String sourceId, String targetId, double amount, TransferUnit unit) {
    interactor.execute(new TransferContentsInputData(sourceId, targetId, amount, unit));
  }
}
