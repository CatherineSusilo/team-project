package usecase.checkquiz;

import java.util.List;

/**
 * How the student did on the quiz.
 */
public final class CheckQuizOutputData {
  private final List<QuestionResult> results;
  private final boolean allCorrect;
  private final boolean submitted;

  /**
   * Creates the output.
   *
   * @param results one result per question, in order
   * @param allCorrect whether every question was answered correctly
   * @param submitted whether progress was saved so the student can move on
   */
  public CheckQuizOutputData(List<QuestionResult> results, boolean allCorrect,
      boolean submitted) {
    this.results = results;
    this.allCorrect = allCorrect;
    this.submitted = submitted;
  }

  public List<QuestionResult> getResults() {
    return results;
  }

  public boolean isAllCorrect() {
    return allCorrect;
  }

  public boolean isSubmitted() {
    return submitted;
  }

  /**
   * How one question went.
   */
  public static final class QuestionResult {
    private final boolean answered;
    private final boolean correct;
    private final String explanation;

    /**
     * Creates the result.
     *
     * @param answered whether the student selected anything
     * @param correct whether the answer is fully correct
     * @param explanation why the correct answer is correct
     */
    public QuestionResult(boolean answered, boolean correct, String explanation) {
      this.answered = answered;
      this.correct = correct;
      this.explanation = explanation;
    }

    public boolean isAnswered() {
      return answered;
    }

    public boolean isCorrect() {
      return correct;
    }

    public String getExplanation() {
      return explanation;
    }
  }
}
