package adapter.viewmodel;

import java.util.List;

/**
 * One item on the bench, as the lab view draws it. The views never import the use case layer,
 * so this has the same fields as the use case's item data.
 */
public final class ItemState {

  private final String id;
  private final String type;
  private final String label;
  private final double horizontal;
  private final double vertical;
  private final List<String> layerPhases;
  private final List<Double> layerVolumes;
  private final boolean hasSolid;
  private final boolean settled;
  private final boolean covered;
  private final boolean cold;
  private final String display;
  private final double ph;

  /**
   * Creates the state.
   *
   * @param id the item's id, "templateId-n"
   * @param type the item's type name
   * @param label the label on it, or "" for none
   * @param x the horizontal position of its centre
   * @param y the vertical position of its centre
   */
  public ItemState(String id, String type, String label, double x, double y) {
    this.id = id;
    this.type = type;
    this.label = label;
    this.horizontal = x;
    this.vertical = y;
    this.layerPhases = List.of();
    this.layerVolumes = List.of();
    this.hasSolid = false;
    this.settled = true;
    this.covered = false;
    this.cold = false;
    this.display = "";
    this.ph = 7;
  }

  /**
   * Creates an item with every fact the screen needs.
   *
   * @param id the item's id
   * @param type the kind of item
   * @param label what is written on it, or "" if nothing
   * @param x where its centre is, horizontally
   * @param y where its centre is, vertically
   * @param layerPhases the phase of each layer inside it, bottom first
   * @param layerVolumes the volume in mL of each layer, in the same order
   * @param hasSolid whether any layer holds undissolved solid
   * @param settled whether its layers have finished separating
   * @param covered whether it has the inverted beaker on top
   * @param cold whether it has been in the ice bath
   * @param display what a scale shows, or "" for anything else
   * @param ph the acidity of what is inside
   */
  public ItemState(String id, String type, String label, double x, double y,
      List<String> layerPhases, List<Double> layerVolumes, boolean hasSolid, boolean settled,
      boolean covered, boolean cold, String display, double ph) {
    this.id = id;
    this.type = type;
    this.label = label;
    this.horizontal = x;
    this.vertical = y;
    this.layerPhases = List.copyOf(layerPhases);
    this.layerVolumes = List.copyOf(layerVolumes);
    this.hasSolid = hasSolid;
    this.settled = settled;
    this.covered = covered;
    this.cold = cold;
    this.display = display;
    this.ph = ph;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getLabel() {
    return label;
  }

  public double getX() {
    return horizontal;
  }

  public double getY() {
    return vertical;
  }

  public List<String> getLayerPhases() {
    return layerPhases;
  }

  public List<Double> getLayerVolumes() {
    return layerVolumes;
  }

  public boolean hasSolid() {
    return hasSolid;
  }

  public boolean isSettled() {
    return settled;
  }

  public boolean isCovered() {
    return covered;
  }

  public boolean isCold() {
    return cold;
  }

  public String getDisplay() {
    return display;
  }

  public double getPh() {
    return ph;
  }
}
