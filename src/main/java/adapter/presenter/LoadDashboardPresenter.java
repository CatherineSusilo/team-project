package adapter.presenter;

import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.PartState;
import adapter.viewmodel.QuestionState;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import usecase.loaddashboard.LoadDashboardOutputBoundary;
import usecase.loaddashboard.LoadDashboardOutputData;
import usecase.loaddashboard.LoadDashboardOutputData.ModuleInfo;
import usecase.loaddashboard.LoadDashboardOutputData.QuestionInfo;

/**
 * Turns the loaded modules into display text for the dashboard.
 */
public class LoadDashboardPresenter implements LoadDashboardOutputBoundary {
  private static final DateTimeFormatter DUE_DATE =
      DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

  private final DashboardViewModel viewModel;
  private final Clock clock;

  /**
   * Creates the presenter.
   *
   * @param viewModel the dashboard's view model
   * @param clock gives today's date, to tell whether a module is late
   */
  public LoadDashboardPresenter(DashboardViewModel viewModel, Clock clock) {
    this.viewModel = viewModel;
    this.clock = clock;
  }

  @Override
  public void prepareSuccessView(LoadDashboardOutputData out) {
    List<ModuleState> modules = new ArrayList<>();
    for (ModuleInfo module : out.getModules()) {
      List<QuestionState> questions = new ArrayList<>();
      for (QuestionInfo question : module.getQuestions()) {
        questions.add(new QuestionState(question.getPrompt(), question.isMultipleChoice(),
            question.isImageChoices(), question.isEquation(), question.getReactants(),
            question.getBlankCount(), question.getChoices(), question.isSavedCorrect()));
      }
      List<PartState> parts = new ArrayList<>();
      parts.add(new PartState("Pre-Procedure Quiz", module.getQuizProgress() + "%"));
      for (int i = 0; i < module.getPartTitles().size(); i++) {
        parts.add(new PartState(module.getPartTitles().get(i),
            module.getPartProgress().get(i) + "%"));
      }
      boolean complete = module.getProgress() == 100;
      modules.add(new ModuleState(module.getTitle(), dueDate(module.getDueDate()),
          complete ? "Complete" : module.getProgress() + "%", complete,
          module.getOverview(), module.getLearningObjectives(),
          parts, module.getSavedAnswers(), questions));
    }
    viewModel.setState(modules, "");
    viewModel.firePropertyChanged();
  }

  /** E.g. "Nov 30, 2026", or "Nov 30, 2026 (3 days late)" once it's past due. */
  private String dueDate(LocalDate due) {
    long late = ChronoUnit.DAYS.between(due, LocalDate.now(clock));
    String text = due.format(DUE_DATE);
    if (late == 1) {
      return text + " (1 day late)";
    }
    return late > 1 ? text + " (" + late + " days late)" : text;
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setState(List.of(), error);
    viewModel.firePropertyChanged();
  }
}
