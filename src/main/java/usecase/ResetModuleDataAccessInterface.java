package usecase;

import entity.LabModule;

/**
 * What ResetModule needs from storage: the modules, the lab, and a way to start the lab over.
 */
public interface ResetModuleDataAccessInterface extends ModuleDataAccessInterface,
    LabDataAccessInterface {

  /**
   * Saves the cleared module and a fresh lab with the same unknown sample and no undo history,
   * in one save.
   *
   * @param module the module, already cleared with {@code clearProgress()}
   * @throws java.io.UncheckedIOException if it can't be saved
   */
  void resetProgress(LabModule module);
}
