package usecase.guessunknown;

import entity.ApiType;

/**
 * The drug the student picked at the end of B7.
 */
public final class GuessUnknownInputData {
  private final ApiType guess;

  /**
   * Creates the input.
   *
   * @param guess the drug the student thinks the unknown is
   */
  public GuessUnknownInputData(ApiType guess) {
    this.guess = guess;
  }

  public ApiType getGuess() {
    return guess;
  }
}
