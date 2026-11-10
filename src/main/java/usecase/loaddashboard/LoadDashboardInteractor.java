package usecase.loaddashboard;

import entity.LabModule;
import entity.QuizQuestion;
import java.util.ArrayList;
import java.util.List;
import usecase.ModuleDataAccessInterface;
import usecase.loaddashboard.LoadDashboardOutputData.ModuleInfo;
import usecase.loaddashboard.LoadDashboardOutputData.QuestionInfo;

/**
 * Loads every module assigned to the student.
 */
public class LoadDashboardInteractor implements LoadDashboardInputBoundary {
  private final ModuleDataAccessInterface dataAccess;
  private final LoadDashboardOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the modules are stored
   * @param presenter receives the modules
   */
  public LoadDashboardInteractor(ModuleDataAccessInterface dataAccess,
      LoadDashboardOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(LoadDashboardInputData in) {
    List<LabModule> modules = dataAccess.getModules();
    if (modules.isEmpty()) {
      presenter.prepareFailView("No modules have been assigned yet.");
      return;
    }
    List<ModuleInfo> infos = new ArrayList<>();
    for (LabModule module : modules) {
      List<QuestionInfo> questions = new ArrayList<>();
      for (QuizQuestion question : module.getPreProcedureQuestions()) {
        questions.add(new QuestionInfo(question.getPrompt(), question.getType().name(),
            question.getChoices()));
      }
      infos.add(new ModuleInfo(module.getTitle(), module.getDueDate(), module.getOverview(),
          module.isCompleted(), module.getLastEdited().orElse(null), questions));
    }
    presenter.prepareSuccessView(new LoadDashboardOutputData(infos));
  }
}
