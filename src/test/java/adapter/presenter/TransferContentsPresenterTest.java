package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.viewmodel.TransferContentsViewModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.transfercontents.TransferContentsOutputData;

/**
 * Tests that the presenter fills the view model and tells the bench to redraw.
 */
class TransferContentsPresenterTest {
  private final TransferContentsViewModel viewModel = new TransferContentsViewModel();
  private final TransferContentsPresenter presenter = new TransferContentsPresenter(viewModel);
  private int redraws;

  TransferContentsPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void successUpdatesTheContainer() {
    presenter.prepareSuccessView(new TransferContentsOutputData("conical",
        List.of("AQUEOUS", "ORGANIC"), false, 13, "Added 10 drops."));
    assertTrue(viewModel.isSuccess());
    assertEquals("conical", viewModel.getTargetId());
    assertEquals(13, viewModel.getPh());
    assertEquals(1, redraws);
  }

  @Test
  void failureShowsTheMessage() {
    presenter.prepareFailView("HCl goes into the aqueous test tube.");
    assertFalse(viewModel.isSuccess());
    assertEquals("HCl goes into the aqueous test tube.", viewModel.getMessage());
    assertEquals(1, redraws);
  }
}
