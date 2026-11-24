package adapter.controller;

import usecase.undo.UndoInputBoundary;
import usecase.undo.UndoInputData;

/**
 * Asks for the student's last action to be undone. The Undo button and the mistake banner both
 * call it.
 */
public class UndoController {
  private final UndoInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor undoes an action
   */
  public UndoController(UndoInputBoundary interactor) {
    this.interactor = interactor;
  }

  /** Undoes one action in the current step. */
  public void execute() {
    interactor.execute(new UndoInputData());
  }
}
