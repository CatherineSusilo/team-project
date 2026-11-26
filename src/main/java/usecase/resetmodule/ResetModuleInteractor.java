package usecase.resetmodule;

import entity.LabModule;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import usecase.LabStateMapper;
import usecase.ResetModuleDataAccessInterface;

/**
 * Clears the student's quiz answers, notebook and lab for a module, keeping their unknown sample.
 */
public class ResetModuleInteractor implements ResetModuleInputBoundary {
  private final ResetModuleDataAccessInterface dataAccess;
  private final ResetModuleOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess the modules and the lab
   * @param presenter shows the result
   */
  public ResetModuleInteractor(ResetModuleDataAccessInterface dataAccess,
      ResetModuleOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(ResetModuleInputData in) {
    Optional<LabModule> found = dataAccess.getModule(in.getModuleTitle());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no module called " + in.getModuleTitle() + ".");
      return;
    }
    LabModule module = found.get();
    List<Set<Integer>> answers = module.getSavedAnswers();
    String notebook = module.getNotebook();
    LocalDateTime lastEdited = module.getLastEdited().orElse(null);
    module.clearProgress();
    try {
      dataAccess.resetProgress(module);
    } catch (UncheckedIOException e) {
      // Not saved, so nothing is reset: the dashboard still matches the module.
      module.restoreProgress(answers, notebook, lastEdited);
      presenter.prepareFailView("Couldn't reset your progress. Please try again.");
      return;
    }
    presenter.prepareSuccessView(new ResetModuleOutputData(module.getTitle(),
        LabStateMapper.from(dataAccess.getLab())));
  }
}
