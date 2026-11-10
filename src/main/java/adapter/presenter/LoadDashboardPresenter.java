package adapter.presenter;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.PartState;
import adapter.viewmodel.QuestionState;
import java.util.ArrayList;
import java.util.List;
import usecase.loaddashboard.LoadDashboardOutputBoundary;
import usecase.loaddashboard.LoadDashboardOutputData;
import usecase.loaddashboard.LoadDashboardOutputData.ModuleInfo;
import usecase.loaddashboard.LoadDashboardOutputData.QuestionInfo;

/**
 * Turns the loaded modules into display text for the dashboard.
 */
public class LoadDashboardPresenter implements LoadDashboardOutputBoundary {
  private final DashboardViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the dashboard's view model
   */
  public LoadDashboardPresenter(DashboardViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(LoadDashboardOutputData out) {
    List<ModuleState> modules = new ArrayList<>();
    for (ModuleInfo module : out.getModules()) {
      List<QuestionState> questions = new ArrayList<>();
      for (QuestionInfo question : module.getQuestions()) {
        questions.add(new QuestionState(question.getPrompt(), question.isMultipleChoice(),
            question.isImageChoices(), question.isEquation(), question.getReactants(),
            question.getBlankCount(), question.getChoices()));
      }
      List<PartState> parts = new ArrayList<>();
      parts.add(new PartState("Pre-Procedure Quiz", module.getQuizProgress() + "%"));
      for (int i = 0; i < module.getPartTitles().size(); i++) {
        parts.add(new PartState(module.getPartTitles().get(i),
            module.getPartProgress().get(i) + "%"));
      }
      boolean complete = module.getProgress() == 100;
      modules.add(new ModuleState(module.getTitle(), module.getDueDate().toString(),
          complete ? "Complete" : module.getProgress() + "%", complete,
          module.getLastEdited() != null, module.getOverview(), module.getLearningObjectives(),
          parts, module.getSavedAnswers(), questions));
    }
    viewModel.setState(modules, "");
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(List.of(), error);
    viewModel.firePropertyChanged();
  }
}
