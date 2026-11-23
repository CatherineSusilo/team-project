package usecase.undo;

/**
 * Steps the student back one action.
 */
public interface UndoInputBoundary {

  /**
   * Undoes the student's last action in the current step.
   *
   * @param in the input data
   */
  void execute(UndoInputData in);
}
