package usecase.checkquiz;

import entity.LabModule;
import entity.QuizQuestion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import usecase.ModuleDataAccessInterface;
import usecase.checkquiz.CheckQuizOutputData.QuestionResult;

/**
 * Checks a module's pre-procedure quiz. Saving progress is SubmitQuiz's job.
 */
public class CheckQuizInteractor implements CheckQuizInputBoundary {
  private final ModuleDataAccessInterface moduleDataAccess;
  private final CheckQuizOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param moduleDataAccess where the modules and their questions are stored
   * @param presenter receives the results
   */
  public CheckQuizInteractor(ModuleDataAccessInterface moduleDataAccess,
      CheckQuizOutputBoundary presenter) {
    this.moduleDataAccess = moduleDataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(CheckQuizInputData in) {
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

    List<QuestionResult> results = new ArrayList<>();
    boolean allCorrect = true;
    for (int i = 0; i < questions.size(); i++) {
      Set<Integer> selected = in.getAnswers().get(i);
      QuizQuestion question = questions.get(i);
      boolean answered = !selected.isEmpty();
      boolean correct = answered && question.isCorrect(selected);
      allCorrect &= correct;
      results.add(new QuestionResult(answered, correct, question.getExplanation()));
    }

    presenter.prepareSuccessView(new CheckQuizOutputData(results, allCorrect));
  }
}
