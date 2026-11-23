package adapter.presenter;

import adapter.viewmodel.UseItemViewModel;
import usecase.useitem.UseItemOutputBoundary;
import usecase.useitem.UseItemOutputData;

/**
 * Turns the result of using an item into what the bench shows: the item's label, cover
 * and scale number, or an error.
 */
public class UseItemPresenter implements UseItemOutputBoundary {
  private final UseItemViewModel viewModel;

  /**
   * Creates the presenter.
   *
   * @param viewModel the bench's use item view model
   */
  public UseItemPresenter(UseItemViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(UseItemOutputData out) {
    viewModel.setResult(out.getItemId(), out.getLabel(), out.isCovered(), out.getDisplay(),
        out.getMessage());
    viewModel.firePropertyChanged();
  }

  @Override
  public void prepareFailView(String error) {
    viewModel.setError(error);
    viewModel.firePropertyChanged();
  }
}
