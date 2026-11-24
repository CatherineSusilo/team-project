package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonLabDataAccessObjectTest {
  @TempDir
  Path folder;

  /** A fresh copy of the module each time, like the app gets on every start. */
  private static LabModule module() {
    return new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "", List.of(), List.of(),
        null, List.of(
            new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
            new QuizQuestion("q2", Type.MULTIPLE, List.of("a", "b", "c"), Set.of(1, 2), "")));
  }

  private JsonLabDataAccessObject open(Path file) {
    return new JsonLabDataAccessObject(file, List.of(module()));
  }

  @Test
  void savedProgressComesBackAfterReopening() {
    Path file = folder.resolve("nested").resolve("save.json");
    JsonLabDataAccessObject first = open(file);
    LabModule module = first.getModule("Pre-lab").orElseThrow();
    module.setSavedAnswers(List.of(Set.of(0), Set.of(1, 2)));
    module.setLastEdited(LocalDateTime.of(2026, 11, 22, 9, 15));
    module.setNotebook("<b>Mass:</b> 0.52 g \"wet\"\n<i>next</i>");
    first.saveModule(module);

    LabModule reopened = open(file).getModule("Pre-lab").orElseThrow();
    assertEquals(List.of(Set.of(0), Set.of(1, 2)), reopened.getSavedAnswers());
    assertEquals(Optional.of(LocalDateTime.of(2026, 11, 22, 9, 15)), reopened.getLastEdited());
    assertEquals(100, reopened.getQuizProgress());
    assertEquals("<b>Mass:</b> 0.52 g \"wet\"\n<i>next</i>", reopened.getNotebook());
  }

  @Test
  void missingFileStartsWithTheModulesAsGiven() {
    LabModule module = open(folder.resolve("save.json")).getModule("Pre-lab").orElseThrow();
    assertEquals(List.of(), module.getSavedAnswers());
    assertEquals(Optional.empty(), module.getLastEdited());
    assertEquals("", module.getNotebook());
  }

  @Test
  void answersForChangedQuestionsAreIgnored() throws IOException {
    Path file = folder.resolve("save.json");
    Files.writeString(file, "{\"modules\":{\"Pre-lab\":{\"savedAnswers\":[[0]],"
        + "\"lastEdited\":\"2026-11-22T09:15\"}}}");
    LabModule module = open(file).getModule("Pre-lab").orElseThrow();
    assertEquals(List.of(), module.getSavedAnswers());
    assertEquals(Optional.of(LocalDateTime.of(2026, 11, 22, 9, 15)), module.getLastEdited());
  }

  @Test
  void unreadableFileIsMovedAsideNotOverwritten() throws IOException {
    Path file = folder.resolve("save.json");
    Files.writeString(file, "not json {");
    JsonLabDataAccessObject dataAccess = open(file);
    assertEquals(List.of(), dataAccess.getModule("Pre-lab").orElseThrow().getSavedAnswers());
    assertEquals("not json {", Files.readString(folder.resolve("save.json.broken")));

    dataAccess.saveModule(dataAccess.getModule("Pre-lab").orElseThrow());
    assertTrue(Files.exists(file));
    assertEquals("not json {", Files.readString(folder.resolve("save.json.broken")));
  }

  @Test
  void fileWithBadValuesIsMovedAsideAndChangesNothing() throws IOException {
    for (String json : List.of(
        "{\"modules\":{\"Pre-lab\":{\"notebook\":\"x\",\"lastEdited\":\"yesterday\"}}}",
        "{\"modules\":{\"Pre-lab\":{\"notebook\":\"x\",\"savedAnswers\":[null,[1]]}}}",
        "{\"modules\":{\"Pre-lab\":{\"savedAnswers\":[[0],[1,null]]}}}")) {
      Path file = folder.resolve("save.json");
      Files.writeString(file, json);
      LabModule module = open(file).getModule("Pre-lab").orElseThrow();
      assertEquals("", module.getNotebook(), json);
      assertEquals(List.of(), module.getSavedAnswers(), json);
      assertFalse(Files.exists(file), json);
      assertEquals(json, Files.readString(folder.resolve("save.json.broken")));
    }
  }

  @Test
  void savingKeepsProgressForModulesThisAppDoesNotHave() throws IOException {
    Path file = folder.resolve("save.json");
    Files.writeString(file, "{\"modules\":{\"Old lab\":{\"notebook\":\"old notes\"}}}");
    JsonLabDataAccessObject dataAccess = open(file);
    dataAccess.saveModule(dataAccess.getModule("Pre-lab").orElseThrow());

    LabModule oldLab = new LabModule("Old lab", LocalDate.of(2026, 10, 1), "", List.of(),
        List.of(), null, List.of());
    new JsonLabDataAccessObject(file, List.of(module(), oldLab));
    assertEquals("old notes", oldLab.getNotebook());
  }

  @Test
  void notebookHtmlIsReadableInTheFile() throws IOException {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject dataAccess = open(file);
    LabModule module = dataAccess.getModule("Pre-lab").orElseThrow();
    module.setNotebook("<pre><b>mass</b></pre>");
    dataAccess.saveModule(module);
    assertTrue(Files.readString(file).contains("<pre><b>mass</b></pre>"));
  }

  @Test
  void failedSaveThrowsAndLeavesNoTemporaryFile() throws IOException {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject dataAccess = open(file);
    // A folder that isn't empty can't be replaced by the save file.
    Files.createDirectories(file.resolve("blocker"));
    assertThrows(UncheckedIOException.class,
        () -> dataAccess.saveModule(dataAccess.getModule("Pre-lab").orElseThrow()));
    try (Stream<Path> files = Files.list(folder)) {
      assertEquals(List.of(file), files.collect(Collectors.toList()));
    }
  }
}
