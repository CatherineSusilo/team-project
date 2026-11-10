package usecase.savequiz;

import entity.LabModule;
import java.time.LocalDateTime;
import java.util.Optional;
import usecase.ModuleDataAccessInterface;

/**
 * Saves the student's quiz answers so they can pick up where they left off. Unlike SubmitQuiz,
 * the answers don't have to be correct.
 */
public class SaveQuizInteractor implements SaveQuizInputBoundary {
  private final ModuleDataAccessInterface moduleDataAccess;
  private final SaveQuizOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param moduleDataAccess where the modules are stored
   * @param presenter receives the result
   */
  public SaveQuizInteractor(ModuleDataAccessInterface moduleDataAccess,
      SaveQuizOutputBoundary presenter) {
    this.moduleDataAccess = moduleDataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(SaveQuizInputData in) {
    Optional<LabModule> found = moduleDataAccess.getModule(in.getModuleTitle());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no module called " + in.getModuleTitle() + ".");
      return;
    }
    LabModule module = found.get();
    int questions = module.getPreProcedureQuestions().size();
    if (in.getAnswers().size() != questions) {
      presenter.prepareFailView("Expected " + questions + " answers but got "
          + in.getAnswers().size() + ".");
      return;
    }

    module.setSavedAnswers(in.getAnswers());
    module.setLastEdited(LocalDateTime.now());
    moduleDataAccess.saveModule(module);
    presenter.prepareSuccessView(new SaveQuizOutputData(module.getTitle()));
  }
}
