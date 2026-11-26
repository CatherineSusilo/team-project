package usecase.resetmodule;

/**
 * Shows the result of resetting a module.
 */
public interface ResetModuleOutputBoundary {

  /**
   * Reports that the module and its lab were reset.
   *
   * @param out the module and the fresh lab
   */
  void prepareSuccessView(ResetModuleOutputData out);

  /**
   * Shows why the module couldn't be reset.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
