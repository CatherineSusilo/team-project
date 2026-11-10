package usecase.submitquiz;

/**
 * The quiz that was submitted.
 */
public final class SubmitQuizOutputData {
  private final String moduleTitle;

  /**
   * Creates the output.
   *
   * @param moduleTitle the module whose progress was saved
   */
  public SubmitQuizOutputData(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }
}
