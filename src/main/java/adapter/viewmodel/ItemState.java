package adapter.viewmodel;

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
