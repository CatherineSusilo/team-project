package entity;

import java.util.List;

/**
 * One manual step made of several parts, done in any order.
 */
public class MultiPartStep extends LabStep {

  private final List<LabStep> parts;

  /**
   * Creates a step.
   *
   * @param id the step's position in the lab, from 0
   * @param title a short name
   * @param instruction what the student is asked to do
   * @param hint what to say while the step is not done
   * @param trayTemplates the template ids the tray offers for this step
   * @param optional whether the student may skip it
   * @param parts the parts; the step is done when every part is
   */
  public MultiPartStep(int id, String title, String instruction, String hint,
      List<String> trayTemplates, boolean optional, List<LabStep> parts) {
    super(id, title, instruction, hint, trayTemplates, optional);
    this.parts = List.copyOf(parts);
  }

  @Override
  public boolean isComplete(Lab lab) {
    return parts.stream().allMatch(part -> part.isComplete(lab));
  }

  @Override
  public boolean expects(Action action) {
    return parts.stream().anyMatch(part -> part.expects(action));
  }

  @Override
  public double waitMinutes(Lab lab) {
    return parts.stream().mapToDouble(part -> part.waitMinutes(lab)).max().orElse(0);
  }

  public List<LabStep> getParts() {
    return parts;
  }
}
