package usecase;

import entity.StepStatus;
import java.util.List;

/**
 * Everything a view needs to draw the lab after any action, so every update shows the same
 * full picture.
 */
public final class LabStateData {

  private final int stepIndex;
  private final int stepCount;
  private final String title;
  private final String instruction;
  private final StepStatus status;
  private final String message;
  private final boolean nextLocked;
  private final boolean prevLocked;
  private final List<String> trayTemplates;
  private final List<ItemData> items;

  /**
   * Creates the data.
   *
   * @param stepIndex the current step, from 0
   * @param stepCount how many steps the lab has
   * @param title the step's name
   * @param instruction what the student is asked to do
   * @param status where the student stands on the step
   * @param message a mistake's hint, or the step's hint
   * @param nextLocked true if Next can't be used yet
   * @param prevLocked true if there is no step to go back to
   * @param trayTemplates the template ids the tray offers now
   * @param items what is on the bench
   */
  public LabStateData(int stepIndex, int stepCount, String title, String instruction,
      StepStatus status, String message, boolean nextLocked, boolean prevLocked,
      List<String> trayTemplates, List<ItemData> items) {
    this.stepIndex = stepIndex;
    this.stepCount = stepCount;
    this.title = title;
    this.instruction = instruction;
    this.status = status;
    this.message = message;
    this.nextLocked = nextLocked;
    this.prevLocked = prevLocked;
    this.trayTemplates = List.copyOf(trayTemplates);
    this.items = List.copyOf(items);
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public int getStepCount() {
    return stepCount;
  }

  public String getTitle() {
    return title;
  }

  public String getInstruction() {
    return instruction;
  }

  public StepStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public boolean isNextLocked() {
    return nextLocked;
  }

  public boolean isPrevLocked() {
    return prevLocked;
  }

  public List<String> getTrayTemplates() {
    return trayTemplates;
  }

  public List<ItemData> getItems() {
    return items;
  }
}
