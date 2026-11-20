package entity;

import java.util.Map;
import java.util.Set;

/**
 * One step of the lab. Each kind of step says whether the student has finished it.
 */
public abstract class LabStep {
  private final String id;
  private final String title;
  private final String instruction;

  /**
   * Creates a step.
   *
   * @param id a short unique name
   * @param title shown above the instruction
   * @param instruction what the student has to do
   */
  protected LabStep(String id, String title, String instruction) {
    this.id = id;
    this.title = title;
    this.instruction = instruction;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getInstruction() {
    return instruction;
  }

  /**
   * Checks the student's work against this step.
   *
   * @param lab the lab as the student left it
   * @return whether the step is complete, incomplete or has a mistake
   */
  public abstract StepResult evaluate(Lab lab);

  /**
   * A nudge for a stuck student. Steps override it with something more useful.
   *
   * @return the hint
   */
  public String getHint() {
    return "Re-read the instruction.";
  }

  /**
   * The substances this step has the student add. Order and target rules use it.
   *
   * @return the substances, empty if the step adds nothing
   */
  public Set<Substance> addedSubstances() {
    return Set.of();
  }

  /**
   * Where each substance must go in this step, by vessel name (e.g. "conical tube").
   * Substances missing from the map can go anywhere.
   *
   * @return substance to vessel name, empty if the step has no restriction
   */
  public Map<Substance, String> requiredTargets() {
    return Map.of();
  }
}
