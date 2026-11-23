package usecase.useitem;

/**
 * The item after it was used, so the screen can redraw it (its label tape, its cover).
 */
public final class UseItemOutputData {
  private final String itemId;
  private final String label;
  private final boolean covered;
  private final String message;

  /**
   * Creates the output.
   *
   * @param itemId the item that changed
   * @param label what its label says now ("" if none)
   * @param covered whether it is covered now
   * @param message what happened, to show the student
   */
  public UseItemOutputData(String itemId, String label, boolean covered, String message) {
    this.itemId = itemId;
    this.label = label;
    this.covered = covered;
    this.message = message;
  }

  public String getItemId() {
    return itemId;
  }

  public String getLabel() {
    return label;
  }

  public boolean isCovered() {
    return covered;
  }

  public String getMessage() {
    return message;
  }
}
