package entity;

/**
 * Anything on the bench. Vessels, the pipette and the dropper bottle extend this.
 */
public class LabItem {

  private final String id;
  private final ItemType type;
  private Vec2 position;

  /**
   * Creates an item.
   *
   * @param id a unique id, such as "conical-1"
   * @param type what kind of item this is
   * @param position where it sits on the bench
   */
  public LabItem(String id, ItemType type, Vec2 position) {
    this.id = id;
    this.type = type;
    this.position = position;
  }

  public String getId() {
    return id;
  }

  public ItemType getType() {
    return type;
  }

  public Vec2 getPosition() {
    return position;
  }

  public void setPosition(Vec2 position) {
    this.position = position;
  }
}