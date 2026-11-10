package usecase.checkquiz;

import java.util.List;

/**
 * How the student did on the quiz.
 *
 * @param results one result per question, in order
 * @param allCorrect whether every question was answered correctly
 * @param submitted whether progress was saved so the student can move on
 */
public record CheckQuizOutputData(List<QuestionResult> results, boolean allCorrect,
    boolean submitted) {

  /**
   * How one question went.
   *
   * @param answered whether the student selected anything
   * @param correct whether the answer is fully correct
   * @param explanation why the correct answer is correct
   */
  public record QuestionResult(boolean answered, boolean correct, String explanation) {
  }
}
