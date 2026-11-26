package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adapter.viewmodel.LabViewModel;
import data.Lab3Definition;
import entity.ApiType;
import entity.ItemType;
import entity.Lab;
import entity.Substances;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.transfercontents.TransferContentsOutputData;

/**
 * Tests that the presenter puts the whole lab on the shared lab view model and redraws once.
 */
class TransferContentsPresenterTest {
  private final LabViewModel viewModel = new LabViewModel();
  private final TransferContentsPresenter presenter = new TransferContentsPresenter(viewModel);
  private int redraws;

  TransferContentsPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void successShowsTheWholeLab() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    Vessel tube = new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    tube.getMixture().add(Substances.ETHER, 4);
    lab.getBench().addItem(tube, new Vec2(0, 0));

    presenter.prepareSuccessView(new TransferContentsOutputData(LabStateMapper.from(lab)));

    assertEquals(lab.getBench().getItems().size(), viewModel.getItems().size());
    assertEquals(1, redraws);
  }

  @Test
  void failureShowsTheMessage() {
    presenter.prepareFailView("HCl goes into the aqueous test tube.");
    assertEquals("HCl goes into the aqueous test tube.", viewModel.getMessage());
    assertEquals(1, redraws);
  }
}
