package usecase;

import entity.Lab;
import entity.LabItem;
import entity.LabStep;
import entity.StepStatus;
import entity.TemplateItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns the lab into the data every lab view update carries.
 */
public final class LabStateMapper {

  private LabStateMapper() {
  }

  /**
   * Describes the lab as it is now.
   *
   * @param lab the lab to describe
   * @return its state
   */
  public static LabStateData from(Lab lab) {
    LabStep step = lab.currentStep();
    StepStatus status = lab.status();
    List<ItemData> items = new ArrayList<>();
    for (LabItem item : lab.getBench().getItems()) {
      String label = item instanceof TemplateItem ? ((TemplateItem) item).getLabel() : "";
      items.add(new ItemData(item.getId(), item.getType().name(), label,
          item.getPosition().getX(), item.getPosition().getY()));
    }
    List<String> titles = new ArrayList<>();
    for (LabStep each : lab.getSteps()) {
      titles.add(each.getTitle());
    }
    boolean nextLocked = status != StepStatus.COMPLETE || !lab.hasNextStep();
    return new LabStateData(lab.getCurrentIndex(), lab.getStepCount(), titles,
        step.getTitle(), step.getInstruction(), status, lab.message(), nextLocked,
        lab.getCurrentIndex() == 0, step.getTrayTemplates(), items);
  }
}
