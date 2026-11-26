package data;

import com.google.gson.JsonParseException;
import entity.Checkpoint;
import entity.Lab;
import entity.LabModule;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import usecase.CheckpointDataAccessInterface;
import usecase.LabDataAccessInterface;
import usecase.ModuleDataAccessInterface;
import usecase.ResetModuleDataAccessInterface;
import usecase.SampleAssignerInterface;

/**
 * Keeps the student's lab, modules and checkpoint questions in memory.
 */
public class InMemoryLabDataAccessObject implements LabDataAccessInterface,
    ModuleDataAccessInterface, CheckpointDataAccessInterface, ResetModuleDataAccessInterface {
  static final int UNDO_POINT = -1;

  // Shared with JsonLabDataAccessObject, so the save file and undo read items the same way.
  final LabJson labJson = new LabJson();
  // Saved lab states, oldest first. A step-start marker carries its step index; an undo point
  // carries UNDO_POINT.
  private final List<Snapshot> snapshots = new ArrayList<>();
  private Lab lab;
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
    this(modules, new RandomAssigner(new Random()));
  }

  /**
   * Starts with the given modules and an unknown sample from the given assigner.
   *
   * @param modules the modules assigned to the student
   * @param assigner picks the student's unknown sample
   */
  public InMemoryLabDataAccessObject(List<LabModule> modules, SampleAssignerInterface assigner) {
    this.modules = new ArrayList<>(modules);
    this.lab = Lab3Definition.create(assigner.assign());
  }

  @Override
  public Lab getLab() {
    return lab;
  }

  @Override
  public void saveLab(Lab lab) {
    this.lab = lab;
  }

  @Override
  public void pushSnapshot() {
    if (snapshots.isEmpty()) {
      markStepStart(0); // Prev needs this to get back to the first step.
    }
    snapshots.add(new Snapshot(labJson.toJson(lab), UNDO_POINT));
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
    // Parse before removing, so a failed restore keeps this undo point.
    lab = restore(last);
    snapshots.remove(snapshots.size() - 1);
    return true;
  }

  @Override
  public void markStepStart(int index) {
    // Undo stops at this marker and Prev lands on a marker, 
    // so earlier undo points are never restored again.
    snapshots.removeIf(snapshot -> snapshot.stepStart == UNDO_POINT);
    snapshots.add(new Snapshot(labJson.toJson(lab), index));
  }

  @Override
  public boolean rewindToStepStart(int index) {
    for (int i = snapshots.size() - 1; i >= 0; i--) {
      if (snapshots.get(i).stepStart == index) {
        lab = restore(snapshots.get(i));
        snapshots.subList(i + 1, snapshots.size()).clear();
        return true;
      }
    }
    return false;
  }

  /** The undo history, oldest first, so the save file can keep it. */
  List<Snapshot> getSnapshots() {
    return List.copyOf(snapshots);
  }

  /**
   * Puts back a saved lab and its undo history, replacing the current ones.
   *
   * @param savedLab the lab as it was saved
   * @param saved the undo history from {@link #getSnapshots()}
   */
  void restoreHistory(Lab savedLab, List<Snapshot> saved) {
    lab = savedLab;
    snapshots.clear();
    snapshots.addAll(saved);
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

  @Override
  public void resetProgress(LabModule module) {
    // There is one lab (Pre-lab 3's), so resetting any module starts it over. Check which
    // module owns the lab once a second lab module exists.
    Lab oldLab = lab;
    List<Snapshot> oldSnapshots = List.copyOf(snapshots);
    lab = Lab3Definition.create(lab.getUnknown());
    snapshots.clear();
    try {
      // JsonLabDataAccessObject.saveModule writes the whole file, so the reset is saved in one
      // write.
      saveModule(module);
    } catch (UncheckedIOException e) {
      // Not saved, so nothing is reset: the lab still matches what the screens show.
      lab = oldLab;
      snapshots.addAll(oldSnapshots);
      throw e;
    }
  }

  private Lab restore(Snapshot snapshot) {
    return readLab(snapshot.json);
  }

  /**
   * Builds a lab back from its JSON text, as the current lab's class. The steps and mistake rules
   * hold tests that can't be saved as JSON, so a restored Lab 3 gets them again from its
   * definition. Undo, Prev and the save file all restore through here.
   *
   * @param json text from a snapshot or the save file
   * @return the restored lab
   * @throws JsonParseException if the text is bad or holds no lab
   */
  Lab readLab(String json) {
    Lab restored = labJson.fromJson(json, lab.getClass());
    if (restored == null) {
      throw new JsonParseException("No lab saved");
    }
    if (restored.getClass() == Lab.class) {
      restored.attachSteps(Lab3Definition.steps());
      restored.attachRules(Lab3Definition.rules());
    }
    return restored;
  }

  /**
   * The lab as JSON text, plus the step it started if this is a step-start marker. It is read
   * back as the current lab's class, so it holds no {@code Class} that would stop it being saved.
   */
  static final class Snapshot {
    private final String json;
    private final int stepStart;

    Snapshot(String json, int stepStart) {
      this.json = json;
      this.stepStart = stepStart;
    }

    String getJson() {
      return json;
    }

    int getStepStart() {
      return stepStart;
    }
  }
}
