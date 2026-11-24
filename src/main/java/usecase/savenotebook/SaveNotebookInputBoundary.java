package usecase.savenotebook;

/**
 * Saves the student's lab notebook for a module.
 */
public interface SaveNotebookInputBoundary {

  /**
   * Saves the notebook.
   *
   * @param in the module and its notebook
   */
  void execute(SaveNotebookInputData in);
}
