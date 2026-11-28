package usecase;

import entity.Lab;
import entity.LabItem;
import entity.LabStep;
import entity.Layer;
import entity.Range;
import entity.Scale;
import entity.StepStatus;
import entity.TemplateItem;
import entity.Vessel;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns the lab into the data every lab view update carries.
 */
public final class LabStateMapper {

  private LabStateMapper() {
  }

  /**
   * Describes one item: where it is, what is written on it and, for a vessel or a scale, what
   * the screen needs to pick its picture.
   */
  private static ItemData describe(LabItem item, Lab lab) {
    String label = item instanceof Vessel ? ((Vessel) item).getLabel()
        : item instanceof TemplateItem ? ((TemplateItem) item).getLabel() : "";
    List<String> phases = new ArrayList<>();
    List<Double> volumes = new ArrayList<>();
    boolean hasSolid = false;
    boolean settled = true;
    boolean covered = false;
    boolean cold = false;
    String display = "";
    double ph = 7;
    if (item instanceof Vessel) {
      Vessel vessel = (Vessel) item;
      for (Layer layer : vessel.getMixture().getLayers()) {
        phases.add(layer.getPhase().name());
        volumes.add(layer.getVolume());
        if (!layer.getSolids().isEmpty()) {
          hasSolid = true;
        }
      }
      settled = vessel.getMixture().isSettled();
      covered = vessel.isCovered();
      cold = vessel.isCold();
      ph = vessel.getMixture().getPh();
    } else if (item instanceof Scale) {
      display = ((Scale) item).display(lab.getBench());
    }
    return new ItemData(item.getId(), item.getType().name(), label,
        item.getPosition().getX(), item.getPosition().getY(), phases, volumes, hasSolid,
        settled, covered, cold, display, ph);
  }

  /**
   * Writes a melting range the way the screen shows it, e.g. "135-136 °C".
   *
   * @param range the measured range, or null if it hasn't been measured
   * @return the text, or "" if there is no range yet
   */
  public static String meltingRange(Range range) {
    if (range == null) {
      return "";
    }
    return String.format("%.0f-%.0f °C", range.getLo(), range.getHi());
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
      items.add(describe(item, lab));
    }
    List<String> titles = new ArrayList<>();
    for (LabStep each : lab.getSteps()) {
      titles.add(each.getTitle());
    }
    boolean nextLocked = status != StepStatus.COMPLETE || !lab.hasNextStep();
    return new LabStateData(lab.getCurrentIndex(), lab.getStepCount(), titles,
        step.getTitle(), step.getInstruction(), status, lab.message(), nextLocked,
        lab.getCurrentIndex() == 0, step.getTrayTemplates(), items, step.waitMinutes(lab),
        meltingRange(lab.getRecord().getMeltingRange()));
  }
}
