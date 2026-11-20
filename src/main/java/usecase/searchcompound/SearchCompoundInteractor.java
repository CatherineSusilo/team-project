package usecase.searchcompound;

import entity.Compound;
import java.io.IOException;
import java.util.Optional;
import usecase.CompoundDataAccessInterface;

/**
 * Looks up a compound by name so the student can check its properties.
 */
public class SearchCompoundInteractor implements SearchCompoundInputBoundary {
  private final CompoundDataAccessInterface dataAccess;
  private final SearchCompoundOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where compounds are looked up
   * @param presenter receives the compound
   */
  public SearchCompoundInteractor(CompoundDataAccessInterface dataAccess,
      SearchCompoundOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(SearchCompoundInputData in) {
    String name = in.getName() == null ? "" : in.getName().trim();
    if (name.isEmpty()) {
      presenter.prepareFailView("Enter a compound name.");
      return;
    }
    Optional<Compound> found;
    try {
      found = dataAccess.findByName(name);
    } catch (IOException e) {
      presenter.prepareFailView(
          "PubChem is unavailable right now. Check your connection and try again.");
      return;
    }
    if (found.isEmpty()) {
      presenter.prepareFailView("No compound named \"" + name + "\" on PubChem.");
      return;
    }
    Compound compound = found.get();
    presenter.prepareSuccessView(new SearchCompoundOutputData(compound.getName(),
        compound.getFormula(), compound.getMolecularWeight(), compound.getMeltingPoint(),
        compound.getBoilingPoint(), compound.getSignal(), compound.getHazardStatements(),
        compound.getStructureImage()));
  }
}
