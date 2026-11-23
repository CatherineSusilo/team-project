package usecase.useitem;

/**
 * The item after it was used, so the screen can redraw it (its label tape, its cover, the
 * scale's number).
 */
public final class UseItemOutputData {
  private final String itemId;
  private final String label;
  private final boolean covered;
  private final String display;
  private final String message;

  /**
   * Creates the output.
   *
   * @param itemId the item that changed
   * @param label what its label says now ("" if none)
   * @param covered whether it is covered now
   * @param display what the scale shows, e.g. "0.000" ("" for anything that isn't a scale)
   * @param message what happened, to show the student
   */
  public UseItemOutputData(String itemId, String label, boolean covered, String display,
      String message) {
    this.itemId = itemId;
    this.label = label;
    this.covered = covered;
    this.display = display;
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

  public String getDisplay() {
    return display;
  }

  public String getMessage() {
    return message;
  }
}
