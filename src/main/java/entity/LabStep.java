package entity;

import java.util.List;

/**
 * One step of the lab manual, such as "A4: weigh 0.200 g". A step looks at the lab to say
 * whether it is done; it holds no progress of its own.
 */
public abstract class LabStep {

  private final int id;
  private final String title;
  private final String instruction;
  private final String hint;
  private final List<String> trayTemplates;
  private final boolean optional;

  /**
   * Creates a step.
   *
   * @param id the step's position in the lab, from 0
   * @param title a short name, such as "A4 Weigh the sample"
   * @param instruction what the student is asked to do
   * @param hint what to say while the step is not done
   * @param trayTemplates the template ids the tray offers for this step
   * @param optional whether the student may skip it
   */
  protected LabStep(int id, String title, String instruction, String hint,
      List<String> trayTemplates, boolean optional) {
    this.id = id;
    this.title = title;
    this.instruction = instruction;
    this.hint = hint;
    this.trayTemplates = List.copyOf(trayTemplates);
    this.optional = optional;
  }

  /**
   * Whether the lab, as it is now, satisfies this step.
   *
   * @param lab the lab to look at
   * @return true if the step is done
   */
  public abstract boolean isComplete(Lab lab);

  /**
   * Whether an action is part of this step.
   *
   * @param action what the student just did
   * @return true if the step expects it
   */
  public abstract boolean expects(Action action);

  /**
   * The total the step allows for its target.
   *
   * @return the allowed range, or null if the step has none
   */
  public Range amountRange() {
    return null;
  }

  /**
   * The substance whose amount {@link #amountRange()} limits.
   *
   * @return water (the aqueous layer), ether (the organic layer) or a reagent; null to limit the
   *     whole volume in the container
   */
  public Substance amountSubstance() {
    return null;
  }

  /**
   * How long the step is waiting, so the timer bar can show.
   *
   * @param lab the lab to look at
   * @return minutes to wait, or 0 when the step is not waiting
   */
  public double waitMinutes(Lab lab) {
    return 0;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getInstruction() {
    return instruction;
  }

  public String getHint() {
    return hint;
  }

  public List<String> getTrayTemplates() {
    return trayTemplates;
  }

  public boolean isOptional() {
    return optional;
  }
}
