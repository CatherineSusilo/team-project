package adapter.controller;

import java.util.List;
import java.util.Set;
import usecase.savequiz.SaveQuizInputBoundary;
import usecase.savequiz.SaveQuizInputData;

/**
 * Passes the student's quiz answers so far on to be saved.
 */
public class SaveQuizController {
  private final SaveQuizInputBoundary interactor;

  /**
   * Creates the controller.
   *
   * @param interactor saves the answers
   */
  public SaveQuizController(SaveQuizInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Saves the answers.
   *
   * @param moduleTitle the module the quiz belongs to
   * @param answers for each question in order, the indexes of the selected choices
   */
  public void execute(String moduleTitle, List<Set<Integer>> answers) {
    interactor.execute(new SaveQuizInputData(moduleTitle, answers));
  }
}
