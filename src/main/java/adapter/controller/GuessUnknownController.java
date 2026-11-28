package adapter.controller;

import entity.ApiType;
import usecase.guessunknown.GuessUnknownInputBoundary;
import usecase.guessunknown.GuessUnknownInputData;

/**
 * Passes the student's pick of the drug (B7) on to be checked.
 */
public class GuessUnknownController {
  private final GuessUnknownInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor checks the guess
   */
  public GuessUnknownController(GuessUnknownInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Checks the pick.
   *
   * @param apiType "ASPIRIN" or "MEFENAMIC_ACID" (plain text, so the view never imports entity)
   */
  public void execute(String apiType) {
    interactor.execute(new GuessUnknownInputData(ApiType.valueOf(apiType)));
  }
}
