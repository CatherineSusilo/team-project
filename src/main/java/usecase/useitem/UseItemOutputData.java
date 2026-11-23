package usecase.useitem;

/**
 * The item after it was used, so the screen can redraw it (its label tape).
 */
public final class UseItemOutputData {
  private final String itemId;
  private final String label;
  private final String message;

  /**
   * Creates the output.
   *
   * @param itemId the item that changed
   * @param label what its label says now ("" if none)
   * @param message what happened, to show the student
   */
  public UseItemOutputData(String itemId, String label, String message) {
    this.itemId = itemId;
    this.label = label;
    this.message = message;
  }

  public String getItemId() {
    return itemId;
  }

  public String getLabel() {
    return label;
  }

  public String getMessage() {
    return message;
  }
}
