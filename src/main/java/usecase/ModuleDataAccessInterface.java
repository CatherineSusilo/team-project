package usecase;

import entity.LabModule;
import java.util.List;
import java.util.Optional;

/**
 * Loads and saves the modules assigned to the student.
 */
public interface ModuleDataAccessInterface {

  /**
   * Returns every module assigned to the student.
   *
   * @return the modules, in display order
   */
  List<LabModule> getModules();

  /**
   * Finds a module by its title.
   *
   * @param title the module's title
   * @return the module, or empty if there is none with that title
   */
  Optional<LabModule> getModule(String title);

  /**
   * Saves a module's changes, such as when it was last edited.
   *
   * @param module the module to save
   */
  void saveModule(LabModule module);
}
