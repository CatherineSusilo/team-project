package usecase.guessunknown;

import usecase.LabStateData;

/**
 * The lab after a right guess, so the screen can show B7 as done. A wrong guess goes to the
 * fail view instead, so this always means the guess was right.
 */
public final class GuessUnknownOutputData {
  private final LabStateData state;

  /**
   * Creates the output.
   *
   * @param state the whole lab after the guess: the step, Next locked or not
   */
  public GuessUnknownOutputData(LabStateData state) {
    this.state = state;
  }

  public LabStateData getState() {
    return state;
  }
}
