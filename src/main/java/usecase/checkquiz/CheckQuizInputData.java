package usecase.checkquiz;

import java.util.List;
import java.util.Set;

/**
 * The student's quiz answers.
 */
public final class CheckQuizInputData {
  private final String moduleTitle;
  private final List<Set<Integer>> answers;
  private final boolean submit;

  /**
   * Creates the input.
   *
   * @param moduleTitle the module the quiz belongs to
   * @param answers for each question in order, the indexes of the selected choices
   * @param submit false to only check the answers (Check Answers), true to also save progress
   *     and move on (Continue)
   */
  public CheckQuizInputData(String moduleTitle, List<Set<Integer>> answers, boolean submit) {
    this.moduleTitle = moduleTitle;
    this.answers = answers;
    this.submit = submit;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }

  public List<Set<Integer>> getAnswers() {
    return answers;
  }

  public boolean isSubmit() {
    return submit;
  }
}
