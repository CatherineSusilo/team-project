package adapter.controller;

import usecase.resetmodule.ResetModuleInputBoundary;
import usecase.resetmodule.ResetModuleInputData;

/**
 * Passes the student's Reset on a module on to be carried out.
 */
public class ResetModuleController {
  private final ResetModuleInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor resets the module
   */
  public ResetModuleController(ResetModuleInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Resets the module's progress.
   *
   * @param moduleTitle the module to reset
   */
  public void execute(String moduleTitle) {
    interactor.execute(new ResetModuleInputData(moduleTitle));
  }
}
