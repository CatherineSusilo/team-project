package usecase.loaddashboard;

import entity.LabModule;
import entity.QuizQuestion;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
      List<Set<Integer>> saved = module.getSavedAnswers();
      for (int i = 0; i < module.getPreProcedureQuestions().size(); i++) {
        QuizQuestion question = module.getPreProcedureQuestions().get(i);
        boolean equation = question.getType() == QuizQuestion.Type.EQUATION;
        questions.add(new QuestionInfo(question.getPrompt(),
            question.getType() == QuizQuestion.Type.MULTIPLE,
            question.getType() == QuizQuestion.Type.IMAGE, equation, question.getReactants(),
            equation ? question.getBlankCount() : 0, question.getChoices(),
            i < saved.size() && question.isCorrect(saved.get(i))));
      }
      infos.add(new ModuleInfo(module.getTitle(), module.getDueDate(), module.getOverview(),
          module.getLearningObjectives(), module.getQuizProgress(), module.getPartTitles(),
          module.getPartProgress(), module.getProgress(), module.getLastEdited().orElse(null),
          module.getSavedAnswers(), questions));
    }
    presenter.prepareSuccessView(new LoadDashboardOutputData(infos));
  }
}
