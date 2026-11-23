package usecase.transfercontents;

import java.util.List;

/**
 * What the container looks like after the transfer, so the screen can redraw it. Layers are
 * listed bottom first (the water layer sits under the ether layer).
 */
public final class TransferContentsOutputData {
  private final String targetId;
  private final List<String> layerPhases;
  private final boolean hasSolid;
  private final double ph;
  private final String message;

  /**
   * Creates the output.
   *
   * @param targetId the container that changed
   * @param layerPhases each layer's phase, bottom first, e.g. ["AQUEOUS", "ORGANIC"]
   * @param hasSolid whether there is undissolved solid in the container
   * @param ph the container's pH
   * @param message what happened, to show the student
   */
  public TransferContentsOutputData(String targetId, List<String> layerPhases,
      boolean hasSolid, double ph, String message) {
    this.targetId = targetId;
    this.layerPhases = List.copyOf(layerPhases);
    this.hasSolid = hasSolid;
    this.ph = ph;
    this.message = message;
  }

  public String getTargetId() {
    return targetId;
  }

  public List<String> getLayerPhases() {
    return layerPhases;
  }

  public boolean hasSolid() {
    return hasSolid;
  }

  public double getPh() {
    return ph;
  }

  public String getMessage() {
    return message;
  }
}
