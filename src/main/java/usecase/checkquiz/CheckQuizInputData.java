package usecase.checkquiz;

import java.util.List;
import java.util.Set;

/**
 * The student's quiz answers.
 */
public final class CheckQuizInputData {
  private final String moduleTitle;
  private final List<Set<Integer>> answers;

  /**
   * Creates the input.
   *
   * @param moduleTitle the module the quiz belongs to
   * @param answers for each question in order, the indexes of the selected choices
   */
  public CheckQuizInputData(String moduleTitle, List<Set<Integer>> answers) {
    this.moduleTitle = moduleTitle;
    this.answers = answers;
  }

  public String getModuleTitle() {
    return moduleTitle;
  }

  public List<Set<Integer>> getAnswers() {
    return answers;
  }
}
