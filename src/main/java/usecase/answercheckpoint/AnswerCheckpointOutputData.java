package usecase.answercheckpoint;

/**
 * How the student did on a checkpoint question.
 */
public final class AnswerCheckpointOutputData {
  private final boolean correct;
  private final String explanation;
  private final String takeaway;

  /**
   * Creates the output.
   *
   * @param correct whether the answer is fully correct, which lets the student continue
   * @param explanation why the right answer is right; empty unless correct, so a wrong
   *     answer doesn't give the solution away
   * @param takeaway the one thing to remember; empty unless correct
   */
  public AnswerCheckpointOutputData(boolean correct, String explanation, String takeaway) {
    this.correct = correct;
    this.explanation = explanation;
    this.takeaway = takeaway;
  }

  public boolean isCorrect() {
    return correct;
  }

  public String getExplanation() {
    return explanation;
  }

  public String getTakeaway() {
    return takeaway;
  }
}
