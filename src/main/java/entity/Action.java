package entity;

/**
 * What the student just did, handed to {@link Lab#record(Action)}.
 */
public final class Action {

  private final ActionKind kind;
  private final String sourceId;
  private final String targetId;
  private final double amount;
  private final Operation operation;
  private final String text;

  /**
   * Creates an action.
   *
   * @param kind what sort of action it was
   * @param sourceId the item acted with
   * @param targetId the item acted on, or null if there was none
   * @param amount mL, mmol or pumps, already converted; 0 if none
   * @param operation which operation, only for {@link ActionKind#USE}; otherwise null
   * @param text label text or a peak name; "" if none
   */
  public Action(ActionKind kind, String sourceId, String targetId, double amount,
      Operation operation, String text) {
    this.kind = kind;
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.amount = amount;
    this.operation = operation;
    this.text = text;
  }

  /**
   * An item was taken from the tray and put on the bench.
   *
   * @param itemId the id of the new item
   * @return the action
   */
  public static Action spawn(String itemId) {
    return new Action(ActionKind.SPAWN, itemId, null, 0, null, "");
  }

  /**
   * An item was moved on the bench.
   *
   * @param itemId the id of the item moved
   * @param nearId the item it ended up beside, or null if none
   * @return the action
   */
  public static Action move(String itemId, String nearId) {
    return new Action(ActionKind.MOVE, itemId, nearId, 0, null, "");
  }

  public ActionKind getKind() {
    return kind;
  }

  public String getSourceId() {
    return sourceId;
  }

  public String getTargetId() {
    return targetId;
  }

  public double getAmount() {
    return amount;
  }

  public Operation getOperation() {
    return operation;
  }

  public String getText() {
    return text;
  }
}
