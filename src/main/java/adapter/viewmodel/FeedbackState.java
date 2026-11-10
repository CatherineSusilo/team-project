package adapter.viewmodel;

/**
 * Feedback under one quiz question.
 */
public final class FeedbackState {
  private final boolean correct;
  private final String verdict;
  private final String explanation;

  /**
   * Creates the feedback.
   *
   * @param correct whether to show it as correct (green) or not (red)
   * @param verdict e.g. "Correct." or "Not answered yet."
   * @param explanation shown after the verdict; empty for skipped questions
   */
  public FeedbackState(boolean correct, String verdict, String explanation) {
    this.correct = correct;
    this.verdict = verdict;
    this.explanation = explanation;
  }

  public boolean isCorrect() {
    return correct;
  }

  public String getVerdict() {
    return verdict;
  }

  public String getExplanation() {
    return explanation;
  }
}
