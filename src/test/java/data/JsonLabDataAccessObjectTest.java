package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Bench;
import entity.ItemType;
import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import entity.Vec2;
import entity.Vessel;
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

  /** How many items are on the bench; a new Lab 3 starts with the unknown's vial. */
  private static int items(JsonLabDataAccessObject dataAccess) {
    return dataAccess.getLab().getBench().getItems().size();
  }

  private static void addBeaker(JsonLabDataAccessObject dataAccess) {
    Bench bench = dataAccess.getLab().getBench();
    bench.addItem(new Vessel(bench.nextId("beaker"), ItemType.BEAKER, new Vec2(0, 0), 100, 30),
        new Vec2(10, 10));
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

  @Test
  void labAndUndoHistoryComeBackAfterReopening() {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject first = open(file);
    first.pushSnapshot();
    addBeaker(first);
    first.saveLab(first.getLab());
    first.pushSnapshot();
    addBeaker(first);
    first.saveLab(first.getLab());

    JsonLabDataAccessObject reopened = open(file);
    assertEquals(3, items(reopened));
    assertTrue(reopened.getLab().currentStep().getTitle().startsWith("A1"));
    assertTrue(reopened.popSnapshot());
    assertEquals(2, items(reopened));
    assertTrue(reopened.popSnapshot());
    assertEquals(1, items(reopened));
    assertTrue(reopened.getLab().currentStep().getTitle().startsWith("A1"));
    assertFalse(reopened.popSnapshot());
  }

  @Test
  void stepStartStillStopsUndoAfterReopening() {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject first = open(file);
    first.pushSnapshot();
    addBeaker(first);
    first.markStepStart(1);

    JsonLabDataAccessObject reopened = open(file);
    assertFalse(reopened.popSnapshot());
    assertEquals(2, items(reopened));
    assertTrue(reopened.rewindToStepStart(0));
    assertEquals(1, items(reopened));
  }

  @Test
  void undoIsSavedWithoutAnotherSaveLab() {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject first = open(file);
    first.pushSnapshot();
    addBeaker(first);
    first.saveLab(first.getLab());
    assertTrue(first.popSnapshot());

    assertEquals(1, items(open(file)));
  }

  @Test
  void savingAModuleKeepsTheLab() {
    Path file = folder.resolve("save.json");
    JsonLabDataAccessObject first = open(file);
    first.pushSnapshot();
    addBeaker(first);
    first.saveLab(first.getLab());
    first.saveModule(first.getModule("Pre-lab").orElseThrow());

    JsonLabDataAccessObject reopened = open(file);
    assertEquals(2, items(reopened));
    assertTrue(reopened.popSnapshot());
    assertEquals(1, items(reopened));
  }

  @Test
  void saveWithoutALabStillLoads() throws IOException {
    Path file = folder.resolve("save.json");
    Files.writeString(file, "{\"modules\":{\"Pre-lab\":{\"notebook\":\"n\"}}}");

    JsonLabDataAccessObject dataAccess = open(file);
    assertEquals("n", dataAccess.getModule("Pre-lab").orElseThrow().getNotebook());
    assertEquals(1, items(dataAccess));
    assertFalse(dataAccess.popSnapshot());
    assertFalse(Files.exists(folder.resolve("save.json.broken")));
  }

  @Test
  void badLabKeepsQuizAndNotebook() throws IOException {
    Path file = folder.resolve("save.json");
    String json = "{\"modules\":{\"Pre-lab\":{\"notebook\":\"n\"}},\"lab\":\"{not json\"}";
    Files.writeString(file, json);

    JsonLabDataAccessObject dataAccess = open(file);
    assertEquals("n", dataAccess.getModule("Pre-lab").orElseThrow().getNotebook());
    assertEquals(1, items(dataAccess));
    assertEquals(json, Files.readString(folder.resolve("save.json.broken")));
  }

  @Test
  void badSnapshotDropsTheLabAndHistory() throws IOException {
    assertLabAndHistoryDropped("[{\"json\":\"{}\",\"stepStart\":0},"
        + "{\"json\":\"{bad\",\"stepStart\":-1}]");
  }

  @Test
  void snapshotWithNoJsonIsRejected() throws IOException {
    assertLabAndHistoryDropped("[{\"stepStart\":-1}]");
  }

  @Test
  void snapshotWithNoStepIsRejected() throws IOException {
    // Without the check, a missing step would read as 0 and turn an undo point into a marker.
    assertLabAndHistoryDropped("[{\"json\":\"{}\"}]");
  }

  @Test
  void snapshotWithAnInvalidStepIsRejected() throws IOException {
    assertLabAndHistoryDropped("[{\"json\":\"{}\",\"stepStart\":-2}]");
  }

  /**
   * A valid lab (an empty bench) with the given undo history loads as a fresh lab (the vial
   * only), and the notebook stays.
   */
  private void assertLabAndHistoryDropped(String snapshots) throws IOException {
    Path file = folder.resolve("save.json");
    Files.writeString(file, "{\"modules\":{\"Pre-lab\":{\"notebook\":\"n\"}},"
        + "\"lab\":\"{}\",\"snapshots\":" + snapshots + "}");

    JsonLabDataAccessObject dataAccess = open(file);
    assertEquals("n", dataAccess.getModule("Pre-lab").orElseThrow().getNotebook());
    assertEquals(1, items(dataAccess));
    assertFalse(dataAccess.popSnapshot());
    assertFalse(dataAccess.rewindToStepStart(0));
    assertTrue(Files.exists(folder.resolve("save.json.broken")));
  }
}
