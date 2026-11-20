package usecase.searchcompound;

/**
 * Searches for a compound.
 */
public interface SearchCompoundInputBoundary {

  /**
   * Looks up the compound and shows its properties.
   *
   * @param in the name to search for
   */
  void execute(SearchCompoundInputData in);
}
