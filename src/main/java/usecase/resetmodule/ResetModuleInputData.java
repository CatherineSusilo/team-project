package usecase.resetmodule;

/**
 * The module whose progress to reset.
 */
public final class ResetModuleInputData {
  private final String moduleTitle;

  /**
   * Creates the input.
   *
   * @param moduleTitle the module to reset
   */
  public ResetModuleInputData(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }
}
