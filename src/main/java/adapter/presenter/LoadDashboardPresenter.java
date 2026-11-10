package adapter.presenter;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.QuestionState;
import java.time.format.DateTimeFormatter;
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
  private static final DateTimeFormatter LAST_MODIFIED_FORMAT =
      DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");

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
    for (ModuleInfo module : out.modules()) {
      List<QuestionState> questions = new ArrayList<>();
      for (QuestionInfo question : module.questions()) {
        questions.add(new QuestionState(question.prompt(), question.type(), question.choices()));
      }
      boolean started = module.lastEdited() != null;
      modules.add(new ModuleState(module.title(), module.dueDate().toString(),
          module.completed() ? "Completed" : "To do",
          started ? LAST_MODIFIED_FORMAT.format(module.lastEdited()) : "NOT STARTED",
          started, module.overview(), questions));
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
