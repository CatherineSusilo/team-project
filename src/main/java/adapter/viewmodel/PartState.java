package adapter.viewmodel;

/**
 * One row of a module's progress list, e.g. "Pre-Procedure Quiz" at "80%".
 */
public final class PartState {
  private final String title;
  private final String progress;

  /**
   * Creates the row.
   *
   * @param title what the row is for, e.g. "Part A"
   * @param progress how much of it is done, e.g. "80%"
   */
  public PartState(String title, String progress) {
    this.title = title;
    this.progress = progress;
  }

  public String getTitle() {
    return title;
  }

  public String getProgress() {
    return progress;
  }
}
