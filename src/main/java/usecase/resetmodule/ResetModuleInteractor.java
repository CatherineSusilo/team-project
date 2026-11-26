package usecase.resetmodule;

import entity.LabModule;
import java.io.UncheckedIOException;
import java.util.Optional;
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
    module.clearProgress();
    try {
      dataAccess.resetProgress(module);
    } catch (UncheckedIOException e) {
      presenter.prepareFailView("Couldn't reset your progress. Please try again.");
      return;
    }
    presenter.prepareSuccessView(new ResetModuleOutputData(module.getTitle(),
        LabStateMapper.from(dataAccess.getLab())));
  }
}
