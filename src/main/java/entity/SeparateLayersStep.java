package entity;

import java.util.List;

/**
 * Moving the bottom (water) layer from one tube to another with a pipette (A8, A9). Done once
 * most of the water is in the new tube and none of the ether came with it.
 */
public class SeparateLayersStep extends LabStep {
  // Allows for rounding in poured volumes.
  private static final double TOLERANCE = 1e-9;

  private final ItemType fromType;
  private final ItemType toType;
  private final double minShare;

  /**
   * Creates the part. It sits inside a {@link MultiPartStep}, which has the step's title, text
   * and tray, so the part has none of its own.
   *
   * @param fromType the tube the water layer comes out of
   * @param toType the tube it goes into
   * @param minShare how much of all the water has to be in the new tube, e.g. 0.8 for 80%
   */
  public SeparateLayersStep(ItemType fromType, ItemType toType, double minShare) {
    super(0, "", "", "", List.of(), false);
    this.fromType = fromType;
    this.toType = toType;
    this.minShare = minShare;
  }

  public double getMinShare() {
    return minShare;
  }

  @Override
  public boolean isComplete(Lab lab) {
    Vessel from = tube(lab, fromType);
    Vessel to = tube(lab, toType);
    if (from == null || to == null) {
      return false;
    }
    double moved = volumeOf(to.getMixture(), Phase.AQUEOUS);
    double left = volumeOf(from.getMixture(), Phase.AQUEOUS);
    // no ether may come along (lab manual: leave a little water behind rather than take ether)
    boolean noEther = volumeOf(to.getMixture(), Phase.ORGANIC) <= TOLERANCE;
    return moved > 0 && noEther && moved >= minShare * (moved + left) - TOLERANCE;
  }

  @Override
  public boolean expects(Action action) {
    // taking a new pipette, moving things, and drawing / emptying the pipette
    return action.getKind() == ActionKind.SPAWN || action.getKind() == ActionKind.MOVE
        || action.getKind() == ActionKind.MOVE_LIQUID;
  }

  // Helper Functions
  // The first tube of that kind on the bench, or null.
  private static Vessel tube(Lab lab, ItemType type) {
    for (LabItem item : lab.getBench().getItems()) {
      if (item.getType() == type) {
        return (Vessel) item;
      }
    }
    return null;
  }

  // How much liquid of one layer (water or ether) is in a mixture, in mL.
  private static double volumeOf(Mixture mixture, Phase phase) {
    double total = 0;
    for (Layer layer : mixture.getLayers()) {
      if (layer.getPhase() == phase) {
        total += layer.getVolume();
      }
    }
    return total;
  }
}
