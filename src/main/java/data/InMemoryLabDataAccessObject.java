package data;

import entity.Lab;
import usecase.LabDataAccessInterface;

/**
 * Keeps the student's lab in memory.
 */
public class InMemoryLabDataAccessObject implements LabDataAccessInterface {
  // TODO: add the UML's SampleAssigner assigner once sample assignment exists.
  private final Lab lab = new Lab();

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public void saveLab(Lab lab) {
    // TODO: save the lab (the scene the student reached and their answers) and set the
    //  module's last-modified time to now. Optionally also write it to a JSON file.
  }
}
