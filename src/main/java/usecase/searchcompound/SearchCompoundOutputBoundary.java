package usecase.searchcompound;

/**
 * Receives the result of a compound search.
 */
public interface SearchCompoundOutputBoundary {

  /**
   * Shows the compound that was found.
   *
   * @param out the compound's properties
   */
  void prepareSuccessView(SearchCompoundOutputData out);

  /**
   * Shows why the search didn't find anything.
   *
   * @param error what went wrong
   */
  void prepareFailView(String error);
}
