package adapter.controller;

import java.util.List;
import java.util.Set;
import usecase.submitquiz.SubmitQuizInputBoundary;
import usecase.submitquiz.SubmitQuizInputData;

/**
 * Passes the student's final quiz answers on to be submitted.
 */
public class SubmitQuizController {
  private final SubmitQuizInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor submits the answers
   */
  public SubmitQuizController(SubmitQuizInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Submits the answers.
   *
   * @param moduleTitle the module the quiz belongs to
   * @param answers for each question in order, the indexes of the selected choices
   */
  public void execute(String moduleTitle, List<Set<Integer>> answers) {
    interactor.execute(new SubmitQuizInputData(moduleTitle, answers));
  }
}
