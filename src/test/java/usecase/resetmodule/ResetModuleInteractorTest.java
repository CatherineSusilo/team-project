package usecase.resetmodule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.InMemoryLabDataAccessObject;
import entity.Lab;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ResetModuleInteractorTest {
  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), List.of(), null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), "")));
  private final InMemoryLabDataAccessObject dataAccess =
      new InMemoryLabDataAccessObject(List.of(module));
  private ResetModuleOutputData output;
  private String error;
  private final ResetModuleOutputBoundary presenter = new ResetModuleOutputBoundary() {
    @Override
    public void prepareSuccessView(ResetModuleOutputData out) {
      output = out;
    }

    @Override
    public void prepareFailView(String message) {
      error = message;
    }
  };

  @Test
  void resetClearsTheModuleAndTheLab() {
    module.setSavedAnswers(List.of(Set.of(0)));
    module.setNotebook("<b>notes</b>");
    module.setLastEdited(LocalDateTime.of(2026, 11, 26, 9, 0));
    Lab before = dataAccess.getLab();

    new ResetModuleInteractor(dataAccess, presenter).execute(new ResetModuleInputData("Pre-lab"));

    assertNull(error);
    assertEquals("Pre-lab", output.getModuleTitle());
    assertEquals(0, output.getState().getStepIndex(), "the lab screen shows the fresh lab");
    assertEquals(List.of(), module.getSavedAnswers());
    assertEquals("", module.getNotebook());
    assertTrue(module.getLastEdited().isEmpty());
    assertTrue(dataAccess.getLab() != before, "a new lab");
    assertEquals(before.getUnknown().getBatch(), dataAccess.getLab().getUnknown().getBatch());
  }

  @Test
  void unknownModuleFailsAndChangesNothing() {
    module.setNotebook("<b>notes</b>");
    Lab before = dataAccess.getLab();

    new ResetModuleInteractor(dataAccess, presenter).execute(new ResetModuleInputData("Nope"));

    assertEquals("There is no module called Nope.", error);
    assertNull(output);
    assertEquals("<b>notes</b>", module.getNotebook());
    assertSame(before, dataAccess.getLab());
  }

  @Test
  void failedSaveReportsTheErrorAndChangesNothing() {
    // Like the JSON DAO on a full disk: the reset runs in memory, then the save throws.
    InMemoryLabDataAccessObject failing = new InMemoryLabDataAccessObject(List.of(module)) {
      @Override
      public void saveModule(LabModule saved) {
        throw new UncheckedIOException(new IOException("disk full"));
      }
    };
    LocalDateTime edited = LocalDateTime.of(2026, 11, 26, 9, 0);
    module.setSavedAnswers(List.of(Set.of(0)));
    module.setNotebook("<b>notes</b>");
    module.setLastEdited(edited);
    Lab before = failing.getLab();

    new ResetModuleInteractor(failing, presenter).execute(new ResetModuleInputData("Pre-lab"));

    assertEquals("Couldn't reset your progress. Please try again.", error);
    assertNull(output);
    assertEquals(List.of(Set.of(0)), module.getSavedAnswers(), "the screens still match");
    assertEquals("<b>notes</b>", module.getNotebook());
    assertEquals(Optional.of(edited), module.getLastEdited());
    assertSame(before, failing.getLab());
  }
}
