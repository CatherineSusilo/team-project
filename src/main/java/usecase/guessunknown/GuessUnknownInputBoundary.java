package usecase.guessunknown;

/**
 * Checks which drug the student thinks the unknown is.
 */
public interface GuessUnknownInputBoundary {
  /**
   * Checks the guess.
   *
   * @param in the drug the student picked
   */
  void execute(GuessUnknownInputData in);
}
