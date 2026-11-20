package usecase.searchcompound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import entity.Compound;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchCompoundInteractorTest {
  private final Compound ether = new Compound("Diethyl Ether", "C4H10O", 74.12, "-116.2 °C",
      "34.6 °C", "Danger", List.of("H224: Extremely flammable liquid and vapor"), null);
  private String searchedFor;
  private boolean offline;
  private SearchCompoundOutputData output;
  private String error;

  // Stands in for PubChem: only knows diethyl ether, and can pretend the wifi is off.
  private final SearchCompoundInteractor interactor = new SearchCompoundInteractor(
      name -> {
        searchedFor = name;
        if (offline) {
          throw new IOException("no network");
        }
        return name.equalsIgnoreCase("diethyl ether") ? Optional.of(ether) : Optional.empty();
      },
      new SearchCompoundOutputBoundary() {
        @Override
        public void prepareSuccessView(SearchCompoundOutputData out) {
          output = out;
        }

        @Override
        public void prepareFailView(String message) {
          error = message;
        }
      });

  @Test
  void foundCompoundShowsItsProperties() {
    interactor.execute(new SearchCompoundInputData("diethyl ether"));
    assertEquals("Diethyl Ether", output.getName());
    assertEquals("C4H10O", output.getFormula());
    assertEquals(74.12, output.getMolecularWeight());
    assertEquals("-116.2 °C", output.getMeltingPoint());
    assertEquals("34.6 °C", output.getBoilingPoint());
    assertEquals("Danger", output.getSignal());
    assertEquals(List.of("H224: Extremely flammable liquid and vapor"),
        output.getHazardStatements());
    assertNull(error);
  }

  @Test
  void trimsInput() {
    interactor.execute(new SearchCompoundInputData("  diethyl ether \n"));
    assertEquals("diethyl ether", searchedFor);
    assertEquals("Diethyl Ether", output.getName());
  }

  @Test
  void blankQueryRejected() {
    interactor.execute(new SearchCompoundInputData("   "));
    assertEquals("Enter a compound name.", error);
    assertNull(searchedFor);
  }

  @Test
  void notFoundPresentsMessage() {
    interactor.execute(new SearchCompoundInputData("unobtainium"));
    assertEquals("No compound named \"unobtainium\" on PubChem.", error);
    assertNull(output);
  }

  @Test
  void unavailablePresentsRetryMessage() {
    offline = true;
    interactor.execute(new SearchCompoundInputData("diethyl ether"));
    assertEquals("PubChem is unavailable right now. Check your connection and try again.",
        error);
    assertNull(output);
  }
}
