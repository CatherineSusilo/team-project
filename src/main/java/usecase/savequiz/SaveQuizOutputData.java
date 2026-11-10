package usecase.savequiz;

/**
 * The quiz whose answers were saved.
 */
public final class SaveQuizOutputData {
  private final String moduleTitle;

  /**
   * Creates the output.
   *
   * @param moduleTitle the module whose answers were saved
   */
  public SaveQuizOutputData(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }
}
