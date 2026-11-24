package usecase.savenotebook;

/**
 * Receives the result of saving a notebook.
 */
public interface SaveNotebookOutputBoundary {

  /**
   * Reports that the notebook is saved.
   *
   * @param out the saved module
   */
  void prepareSuccessView(SaveNotebookOutputData out);

  /**
   * Shows why the notebook couldn't be saved.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
