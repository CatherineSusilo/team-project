package usecase.searchcompound;

/**
 * The name the student searched for.
 */
public final class SearchCompoundInputData {
  private final String name;

  /**
   * Creates the input.
   *
   * @param name what the student typed, e.g. "diethyl ether"
   */
  public SearchCompoundInputData(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
