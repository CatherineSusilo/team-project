package usecase.resetmodule;

import usecase.LabStateData;

/**
 * The module that was reset, and the fresh lab the lab screen should show.
 */
public final class ResetModuleOutputData {
  private final String moduleTitle;
  private final LabStateData state;

  /**
   * Creates the output.
   *
   * @param moduleTitle the module that was reset
   * @param state the fresh lab, back at its first step
   */
  public ResetModuleOutputData(String moduleTitle, LabStateData state) {
    this.moduleTitle = moduleTitle;
    this.state = state;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }

  public LabStateData getState() {
    return state;
  }
}
