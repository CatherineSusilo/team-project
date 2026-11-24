package usecase.savenotebook;

/**
 * The module whose notebook was saved.
 */
public final class SaveNotebookOutputData {
  private final String moduleTitle;

  /**
   * Creates the output.
   *
   * @param moduleTitle the module whose notebook was saved
   */
  public SaveNotebookOutputData(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }
}
