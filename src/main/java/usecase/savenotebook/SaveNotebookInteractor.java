package usecase.savenotebook;

import entity.LabModule;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.Optional;
import usecase.ModuleDataAccessInterface;

/**
 * Saves a module's lab notebook and marks the module as just edited.
 */
public class SaveNotebookInteractor implements SaveNotebookInputBoundary {
  private final ModuleDataAccessInterface moduleDataAccess;
  private final SaveNotebookOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param moduleDataAccess loads and saves the modules
   * @param presenter shows the result
   */
  public SaveNotebookInteractor(ModuleDataAccessInterface moduleDataAccess,
      SaveNotebookOutputBoundary presenter) {
    this.moduleDataAccess = moduleDataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(SaveNotebookInputData in) {
    Optional<LabModule> found = moduleDataAccess.getModule(in.getModuleTitle());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no module called " + in.getModuleTitle() + ".");
      return;
    }
    LabModule module = found.get();
    module.setNotebook(in.getHtml());
    module.setLastEdited(LocalDateTime.now());
    try {
      moduleDataAccess.saveModule(module);
    } catch (UncheckedIOException e) {
      presenter.prepareFailView("Couldn't save your notebook. Please try again.");
      return;
    }
    presenter.prepareSuccessView(new SaveNotebookOutputData(module.getTitle()));
  }
}
