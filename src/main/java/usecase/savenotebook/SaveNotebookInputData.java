package usecase.savenotebook;

/**
 * The student's lab notebook, sent when they leave it.
 */
public final class SaveNotebookInputData {
  private final String moduleTitle;
  private final String html;

  /**
   * Creates the input.
   *
   * @param moduleTitle the module the notebook belongs to
   * @param html the notebook's contents as HTML, or empty if nothing is written
   */
  public SaveNotebookInputData(String moduleTitle, String html) {
    this.moduleTitle = moduleTitle;
    this.html = html;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }

  public String getHtml() {
    return html;
  }
}
