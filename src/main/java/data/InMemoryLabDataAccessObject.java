package data;

import entity.Lab;
import entity.LabModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import usecase.LabDataAccessInterface;
import usecase.ModuleDataAccessInterface;

/**
 * Keeps the student's lab and modules in memory.
 */
public class InMemoryLabDataAccessObject implements LabDataAccessInterface,
    ModuleDataAccessInterface {
  // TODO: add the UML's SampleAssigner assigner once sample assignment exists.
  private final Lab lab = new Lab();
  private final List<LabModule> modules;

  /**
   * Starts with the Pre-lab 3 module.
   */
  public InMemoryLabDataAccessObject() {
    this(List.of(PreLab3Quiz.module()));
  }

  /**
   * Starts with the given modules.
   *
   * @param modules the modules assigned to the student
   */
  public InMemoryLabDataAccessObject(List<LabModule> modules) {
    this.modules = new ArrayList<>(modules);
  }

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public void saveLab(Lab lab) {
    // TODO: save the lab (the scene the student reached and their answers).
    //  Optionally also write it to a JSON file.
  }

  @Override
  public List<LabModule> getModules() {
    return List.copyOf(modules);
  }

  @Override
  public Optional<LabModule> getModule(String title) {
    return modules.stream().filter(module -> module.getTitle().equals(title)).findFirst();
  }

  @Override
  public void saveModule(LabModule module) {
    modules.replaceAll(saved -> saved.getTitle().equals(module.getTitle()) ? module : saved);
  }
}
