package data;

import entity.Checkpoint;
import entity.Lab;
import entity.LabModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import usecase.CheckpointDataAccessInterface;
import usecase.LabDataAccessInterface;
import usecase.ModuleDataAccessInterface;

/**
 * Keeps the student's lab, modules and checkpoint questions in memory.
 */
public class InMemoryLabDataAccessObject implements LabDataAccessInterface,
    ModuleDataAccessInterface, CheckpointDataAccessInterface {
  // TODO: add the UML's SampleAssigner assigner once sample assignment exists.
  private static final int UNDO_POINT = -1;

  private final LabJson labJson = new LabJson();
  // Saved lab states, oldest first. A step-start marker carries its step index; an undo point
  // carries UNDO_POINT.
  private final List<Snapshot> snapshots = new ArrayList<>();
  private Lab lab = new Lab();
  private final List<LabModule> modules;
  private final List<Checkpoint> checkpoints = PreLab3Checkpoints.checkpoints();

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
    // TODO: optionally also write the lab and its snapshots to a JSON file.
    this.lab = lab;
  }

  @Override
  public void pushSnapshot() {
    snapshots.add(new Snapshot(labJson.toJson(lab), lab.getClass(), UNDO_POINT));
  }

  @Override
  public boolean popSnapshot() {
    if (snapshots.isEmpty()) {
      return false;
    }
    Snapshot last = snapshots.get(snapshots.size() - 1);
    if (last.stepStart != UNDO_POINT) {
      return false;
    }
    snapshots.remove(snapshots.size() - 1);
    lab = labJson.fromJson(last.json, last.labClass);
    return true;
  }

  @Override
  public void markStepStart(int index) {
    snapshots.add(new Snapshot(labJson.toJson(lab), lab.getClass(), index));
  }

  @Override
  public boolean rewindToStepStart(int index) {
    for (int i = snapshots.size() - 1; i >= 0; i--) {
      if (snapshots.get(i).stepStart == index) {
        lab = labJson.fromJson(snapshots.get(i).json, snapshots.get(i).labClass);
        snapshots.subList(i + 1, snapshots.size()).clear();
        return true;
      }
    }
    return false;
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
  public Optional<Checkpoint> getCheckpoint(int stepIndex) {
    return checkpoints.stream().filter(checkpoint -> checkpoint.getStepIndex() == stepIndex)
        .findFirst();
  }

  @Override
  public void saveModule(LabModule module) {
    modules.replaceAll(saved -> saved.getTitle().equals(module.getTitle()) ? module : saved);
  }

  /** The lab as JSON text, plus the step it started if this is a step-start marker. */
  private static final class Snapshot {
    private final String json;
    private final Class<? extends Lab> labClass;
    private final int stepStart;

    Snapshot(String json, Class<? extends Lab> labClass, int stepStart) {
      this.json = json;
      this.labClass = labClass;
      this.stepStart = stepStart;
    }
  }
}
