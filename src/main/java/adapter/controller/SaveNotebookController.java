package adapter.controller;

import usecase.savenotebook.SaveNotebookInputBoundary;
import usecase.savenotebook.SaveNotebookInputData;

/**
 * Passes the student's lab notebook on to be saved.
 */
public class SaveNotebookController {
  private final SaveNotebookInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor saves the notebook
   */
  public SaveNotebookController(SaveNotebookInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Saves the notebook.
   *
   * @param moduleTitle the module the notebook belongs to
   * @param html the notebook's contents as HTML, or empty if nothing is written
   */
  public void execute(String moduleTitle, String html) {
    interactor.execute(new SaveNotebookInputData(moduleTitle, html));
  }
}
