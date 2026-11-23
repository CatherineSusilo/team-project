package usecase;

/**
 * One item on the bench, as the views need to see it.
 */
public final class ItemData {

  private final String id;
  private final String type;
  private final String label;
  private final double horizontal;
  private final double vertical;

  /**
   * Creates the data.
   *
   * @param id the item's id, "templateId-n"
   * @param type the item's {@code ItemType} name
   * @param label the label on it, or "" for none
   * @param x the horizontal position of its centre
   * @param y the vertical position of its centre
   */
  public ItemData(String id, String type, String label, double x, double y) {
    this.id = id;
    this.type = type;
    this.label = label;
    this.horizontal = x;
    this.vertical = y;
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
}
