package adapter.controller;

import java.util.List;
import java.util.Set;
import usecase.checkquiz.CheckQuizInputBoundary;
import usecase.checkquiz.CheckQuizInputData;

/**
 * Passes the student's quiz answers on to be checked.
 */
public class CheckQuizController {
  private final CheckQuizInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor checks the answers
   */
  public CheckQuizController(CheckQuizInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Checks the answers.
   *
   * @param moduleTitle the module the quiz belongs to
   * @param answers for each question in order, the indexes of the selected choices
   */
  public void execute(String moduleTitle, List<Set<Integer>> answers) {
    interactor.execute(new CheckQuizInputData(moduleTitle, answers));
  }
}
