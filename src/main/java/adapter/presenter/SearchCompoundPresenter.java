package adapter.presenter;

import adapter.viewmodel.CompoundViewModel;
import java.util.Locale;
import usecase.searchcompound.SearchCompoundOutputBoundary;
import usecase.searchcompound.SearchCompoundOutputData;

/**
 * Turns a found compound into display text for the search view.
 */
public class SearchCompoundPresenter implements SearchCompoundOutputBoundary {
  private static final String NOT_AVAILABLE = "Not available";

  private final CompoundViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the compound search's view model
   */
  public SearchCompoundPresenter(CompoundViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(SearchCompoundOutputData out) {
    // Locale.ROOT so the weight always uses a dot, even on a French or German laptop.
    viewModel.setCompound(out.getName(), out.getFormula(),
        String.format(Locale.ROOT, "%.2f g/mol", out.getMolecularWeight()),
        orNotAvailable(out.getMeltingPoint()), orNotAvailable(out.getBoilingPoint()),
        orNotAvailable(out.getSignal()), out.getHazardStatements(), out.getStructureImage());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setError(error);
    viewModel.firePropertyChanged();
  }

  private static String orNotAvailable(String value) {
    return value == null || value.isBlank() ? NOT_AVAILABLE : value;
  }
}
