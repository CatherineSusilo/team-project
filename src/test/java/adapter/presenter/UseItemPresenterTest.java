package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.viewmodel.UseItemViewModel;
import org.junit.jupiter.api.Test;
import usecase.useitem.UseItemOutputData;

/**
 * Tests that the presenter fills the view model and tells the bench to redraw.
 */
class UseItemPresenterTest {
  private final UseItemViewModel viewModel = new UseItemViewModel();
  private final UseItemPresenter presenter = new UseItemPresenter(viewModel);
  private int redraws;

  UseItemPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void successShowsTheLabel() {
    presenter.prepareSuccessView(new UseItemOutputData("conical", "A", "Labelled it A."));
    assertTrue(viewModel.isSuccess());
    assertEquals("conical", viewModel.getItemId());
    assertEquals("A", viewModel.getLabel());
    assertEquals("Labelled it A.", viewModel.getMessage());
    assertEquals(1, redraws);
  }

  @Test
  void failureShowsTheMessage() {
    presenter.prepareFailView("Write something on the label.");
    assertFalse(viewModel.isSuccess());
    assertEquals("Write something on the label.", viewModel.getMessage());
    assertEquals(1, redraws);
  }

  @Test
  void failureAfterSuccessKeepsTheLabel() {
    presenter.prepareSuccessView(new UseItemOutputData("conical", "A", "Labelled it A."));
    presenter.prepareFailView("Write something on the label.");
    assertFalse(viewModel.isSuccess());
    assertEquals("A", viewModel.getLabel());                    // the tube still says "A"
    assertEquals(2, redraws);
  }
}
