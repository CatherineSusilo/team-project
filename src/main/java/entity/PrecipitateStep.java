package entity;

import java.util.List;

/**
 * B2: the test tube cools in the ice bath for 10 minutes, so the drug comes out as a solid
 * (the solid itself already formed when HCl was stirred in; this is the wait). While the tube
 * is in the bath, the timer bar shows the 10 minutes.
 */
public class PrecipitateStep extends LabStep {
  private final ItemType tubeType;
  private final double minutes;

  /**
   * Creates the part. It sits inside a {@link MultiPartStep}, which has the step's title, text
   * and tray, so the part has none of its own.
   *
   * @param tubeType the kind of tube that cools, e.g. the test tube
   * @param minutes how long it has to be in the bath, in lab minutes
   */
  public PrecipitateStep(ItemType tubeType, double minutes) {
    super(0, "", "", "", List.of(), false);
    this.tubeType = tubeType;
    this.minutes = minutes;
  }

  public double getMinutes() {
    return minutes;
  }

  @Override
  public boolean isComplete(Lab lab) {
    return cooledFor(lab) >= minutes;
  }

  @Override
  public boolean expects(Action action) {
    // taking things out and moving the tube into the bath
    return action.getKind() == ActionKind.SPAWN || action.getKind() == ActionKind.MOVE;
  }

  @Override
  public double waitMinutes(Lab lab) {
    // the timer bar shows only while the tube is in the bath and hasn't had its 10 minutes
    if (isComplete(lab) || !inTheBath(lab)) {
      return 0;
    }
    return minutes;
  }

  // Helper Functions
  // How long the tube has been in an ice bath, in lab minutes (0 if there's no tube or bath).
  private double cooledFor(Lab lab) {
    String tubeId = tubeId(lab);
    double most = 0;
    for (LabItem item : lab.getBench().getItems()) {
      if (tubeId != null && item.getType() == ItemType.ICE_BATH) {
        // ask each ice bath how long the tube has been in it; keep the longest (usually there's
        // only one bath, but a student could drag out two)
        most = Math.max(most, ((IceBath) item).minutesImmersed(tubeId));
      }
    }
    return most;
  }

  // Whether the tube is sitting in an ice bath right now.
  private boolean inTheBath(Lab lab) {
    String tubeId = tubeId(lab);
    for (LabItem item : lab.getBench().getItems()) {
      if (tubeId != null && item.getType() == ItemType.ICE_BATH
          && ((IceBath) item).isImmersed(tubeId)) {
        return true;
      }
    }
    return false;
  }

  // The first tube of that kind on the bench, or null.
  private String tubeId(Lab lab) {
    for (LabItem item : lab.getBench().getItems()) {
      if (item.getType() == tubeType) {
        return item.getId();
      }
    }
    return null;
  }
}
