package usecase.moveitem;

/**
 * Which item the student moved, and where it was let go.
 */
public class MoveItemInputData {

  private final String itemId;
  private final double horizontal;
  private final double vertical;

  /**
   * Creates the data.
   *
   * @param itemId the item's id
   * @param x where it was let go, horizontally
   * @param y where it was let go, vertically
   */
  public MoveItemInputData(String itemId, double x, double y) {
    this.itemId = itemId;
    this.horizontal = x;
    this.vertical = y;
  }

  public String getItemId() {
    return itemId;
  }

  public double getX() {
    return horizontal;
  }

  public double getY() {
    return vertical;
  }
}
