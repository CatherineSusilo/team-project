package adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.viewmodel.CompoundViewModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import usecase.searchcompound.SearchCompoundOutputData;

class SearchCompoundPresenterTest {
  private final CompoundViewModel viewModel = new CompoundViewModel();
  private final SearchCompoundPresenter presenter = new SearchCompoundPresenter(viewModel);
  private int redraws;

  SearchCompoundPresenterTest() {
    viewModel.addPropertyChangeListener(event -> redraws++);
  }

  @Test
  void showsPropertiesAsDisplayText() {
    presenter.prepareSuccessView(new SearchCompoundOutputData("Diethyl Ether", "C4H10O", 74.1,
        "-116.2 °C", "34.6 °C", "Danger", List.of("H224: Extremely flammable"), null));
    assertTrue(viewModel.isFound());
    assertEquals("Diethyl Ether", viewModel.getName());
    assertEquals("74.10 g/mol", viewModel.getMolecularWeight());
    assertEquals("-116.2 °C", viewModel.getMeltingPoint());
    assertEquals("34.6 °C", viewModel.getBoilingPoint());
    assertEquals("Danger", viewModel.getSignal());
    assertEquals(List.of("H224: Extremely flammable"), viewModel.getHazardStatements());
    assertEquals("", viewModel.getMessage());
    assertEquals(1, redraws);
  }

  @Test
  void missingDataSaysNotAvailable() {
    presenter.prepareSuccessView(new SearchCompoundOutputData("Water", "H2O", 18.015, null,
        "", null, List.of(), null));
    assertEquals("18.02 g/mol", viewModel.getMolecularWeight());
    assertEquals("Not available", viewModel.getMeltingPoint());
    assertEquals("Not available", viewModel.getBoilingPoint());
    assertEquals("Not available", viewModel.getSignal());
    assertTrue(viewModel.getHazardStatements().isEmpty());
  }

  @Test
  void failureClearsTheLastCompound() {
    presenter.prepareSuccessView(new SearchCompoundOutputData("Water", "H2O", 18.015, null,
        null, null, List.of(), new byte[] {1}));
    presenter.prepareFailView("No compound named \"x\" on PubChem.");
    assertFalse(viewModel.isFound());
    assertEquals("", viewModel.getName());
    assertNull(viewModel.getStructureImage());
    assertEquals("No compound named \"x\" on PubChem.", viewModel.getMessage());
    assertEquals(2, redraws);
  }
}
