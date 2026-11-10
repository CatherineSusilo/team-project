package usecase.checkquiz;

import entity.LabModule;
import entity.QuizQuestion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import usecase.LabDataAccessInterface;
import usecase.ModuleDataAccessInterface;
import usecase.checkquiz.CheckQuizOutputData.QuestionResult;

/**
 * Checks a module's pre-procedure quiz and, on Continue, saves the student's progress.
 */
public class CheckQuizInteractor implements CheckQuizInputBoundary {
  private final ModuleDataAccessInterface moduleDataAccess;
  private final LabDataAccessInterface labDataAccess;
  private final CheckQuizOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param moduleDataAccess where the modules and their questions are stored
   * @param labDataAccess saves the student's lab when they continue
   * @param presenter receives the results
   */
  public CheckQuizInteractor(ModuleDataAccessInterface moduleDataAccess,
      LabDataAccessInterface labDataAccess, CheckQuizOutputBoundary presenter) {
    this.moduleDataAccess = moduleDataAccess;
    this.labDataAccess = labDataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(CheckQuizInputData in) {
    Optional<LabModule> found = moduleDataAccess.getModule(in.moduleTitle());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no module called " + in.moduleTitle() + ".");
      return;
    }
    LabModule module = found.get();
    List<QuizQuestion> questions = module.getPreProcedureQuestions();
    if (in.answers().size() != questions.size()) {
      presenter.prepareFailView("Expected " + questions.size() + " answers but got "
          + in.answers().size() + ".");
      return;
    }

    List<QuestionResult> results = new ArrayList<>();
    boolean allCorrect = true;
    for (int i = 0; i < questions.size(); i++) {
      Set<Integer> selected = in.answers().get(i);
      QuizQuestion question = questions.get(i);
      boolean answered = !selected.isEmpty();
      boolean correct = answered && question.isCorrect(selected);
      allCorrect &= correct;
      results.add(new QuestionResult(answered, correct, question.getExplanation()));
    }

    if (in.submit()) {
      if (!allCorrect) {
        presenter.prepareFailView("Answer every question correctly before continuing.");
        return;
      }
      module.setLastEdited(LocalDateTime.now());
      moduleDataAccess.saveModule(module);
      labDataAccess.saveLab(labDataAccess.getLab());
    }
    presenter.prepareSuccessView(new CheckQuizOutputData(results, allCorrect, in.submit()));
  }
}
