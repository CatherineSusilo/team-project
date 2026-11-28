package usecase.guessunknown;

/**
 * What the GuessUnknown use case tells the presenter.
 */
public interface GuessUnknownOutputBoundary {
  /**
   * Shows the lab after a right guess.
   *
   * @param out the new state
   */
  void prepareSuccessView(GuessUnknownOutputData out);

  /**
   * Shows why the guess didn't count: the hint after a wrong pick, or what to do first.
   *
   * @param error the hint or reason
   */
  void prepareFailView(String error);
}
