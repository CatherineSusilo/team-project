package adapter.controller;

import usecase.searchcompound.SearchCompoundInputBoundary;
import usecase.searchcompound.SearchCompoundInputData;

/**
 * Passes the student's compound search on to the use case.
 */
public class SearchCompoundController {
  private final SearchCompoundInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor looks up the compound
   */
  public SearchCompoundController(SearchCompoundInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Searches for a compound. This waits on the network, so don't call it on the Swing thread.
   *
   * @param name what the student typed
   */
  public void execute(String name) {
    interactor.execute(new SearchCompoundInputData(name));
  }
}
