package usecase.resetmodule;

/**
 * Clears the student's progress on a module and starts its lab over.
 */
public interface ResetModuleInputBoundary {

  /**
   * Resets the module.
   *
   * @param in the module to reset
   */
  void execute(ResetModuleInputData in);
}
