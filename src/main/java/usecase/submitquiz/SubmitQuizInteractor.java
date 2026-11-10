package usecase.submitquiz;

import entity.LabModule;
import entity.QuizQuestion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import usecase.LabDataAccessInterface;
import usecase.ModuleDataAccessInterface;

/**
 * Saves the student's progress once every quiz answer is correct.
 */
public class SubmitQuizInteractor implements SubmitQuizInputBoundary {
  private final ModuleDataAccessInterface moduleDataAccess;
  private final LabDataAccessInterface labDataAccess;
  private final SubmitQuizOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param moduleDataAccess where the modules and their questions are stored
   * @param labDataAccess saves the student's lab
   * @param presenter receives the result
   */
  public SubmitQuizInteractor(ModuleDataAccessInterface moduleDataAccess,
      LabDataAccessInterface labDataAccess, SubmitQuizOutputBoundary presenter) {
    this.moduleDataAccess = moduleDataAccess;
    this.labDataAccess = labDataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(SubmitQuizInputData in) {
    Optional<LabModule> found = moduleDataAccess.getModule(in.getModuleTitle());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no module called " + in.getModuleTitle() + ".");
      return;
    }
    LabModule module = found.get();
    List<QuizQuestion> questions = module.getPreProcedureQuestions();
    if (in.getAnswers().size() != questions.size()) {
      presenter.prepareFailView("Expected " + questions.size() + " answers but got "
          + in.getAnswers().size() + ".");
      return;
    }
    for (int i = 0; i < questions.size(); i++) {
      if (!questions.get(i).isCorrect(in.getAnswers().get(i))) {
        presenter.prepareFailView("Answer every question correctly before continuing.");
        return;
      }
    }

    // TODO: save quiz progress: module.setSavedAnswers(in.getAnswers()) so the quiz shows
    //  100% and reopens with these answers.
    module.setLastEdited(LocalDateTime.now());
    moduleDataAccess.saveModule(module);
    labDataAccess.saveLab(labDataAccess.getLab());
    presenter.prepareSuccessView(new SubmitQuizOutputData(module.getTitle()));
  }
}
