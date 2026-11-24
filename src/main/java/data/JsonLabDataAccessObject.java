package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import data.InMemoryLabDataAccessObject.Snapshot;
import entity.Lab;
import entity.LabModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keeps the student's lab, modules and checkpoint questions in memory, and writes the student's
 * progress to a JSON file so it is still there when the app opens again. Only progress is saved
 * (each module's answers, notebook and when it was last edited, plus the lab and its undo
 * history); the module content stays in code.
 */
public class JsonLabDataAccessObject extends InMemoryLabDataAccessObject {
  private final Path file;
  // No HTML escaping, so notebooks read as written ("<b>", not "\u003cb\u003e").
  private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
  private final LabJson labJson = new LabJson();
  /** Saved progress for modules this app doesn't have, kept so a save doesn't erase it. */
  private final Map<String, ModuleProgress> otherModules = new LinkedHashMap<>();

  /**
   * Starts with the Pre-lab 3 module and the progress saved in the file.
   *
   * @param file the save file; it is created on the first save
   */
  public JsonLabDataAccessObject(Path file) {
    this(file, List.of(PreLab3Quiz.module()));
  }

  /**
   * Starts with the given modules and the progress saved in the file. If the file can't be
   * read as a save file (bad JSON or bad values), it is moved aside to {@code <file>.broken}
   * and the student starts fresh, so the next save doesn't overwrite it. If only the lab or its
   * undo history is bad, the file is copied there instead and only the lab starts fresh.
   *
   * @param file the save file; it is created on the first save
   * @param modules the modules assigned to the student
   */
  public JsonLabDataAccessObject(Path file, List<LabModule> modules) {
    super(modules);
    this.file = file;
    load();
  }

  /**
   * Starts with the given modules and lab, then the progress saved in the file. A saved lab is
   * read back as the same class as {@code startingLab}.
   *
   * @param file the save file; it is created on the first save
   * @param modules the modules assigned to the student
   * @param startingLab the lab to use if the file has none
   */
  JsonLabDataAccessObject(Path file, List<LabModule> modules, Lab startingLab) {
    super(modules);
    this.file = file;
    restoreHistory(startingLab, List.of());
    load();
  }

  @Override
  public void saveModule(LabModule module) {
    super.saveModule(module);
    write();
  }

  @Override
  public void saveLab(Lab lab) {
    super.saveLab(lab);
    write();
  }

  // pushSnapshot doesn't write: the change it comes before is saved by the next saveLab.

  @Override
  public boolean popSnapshot() {
    boolean undone = super.popSnapshot();
    if (undone) {
      write();
    }
    return undone;
  }

  @Override
  public void markStepStart(int index) {
    super.markStepStart(index);
    write();
  }

  @Override
  public boolean rewindToStepStart(int index) {
    boolean rewound = super.rewindToStepStart(index);
    if (rewound) {
      write();
    }
    return rewound;
  }

  private void load() {
    if (!Files.exists(file)) {
      return;
    }
    String json;
    try {
      json = Files.readString(file);
    } catch (IOException e) {
      throw new UncheckedIOException("Couldn't read " + file, e);
    }
    // Check every value before changing any module, so a bad file changes nothing.
    List<Runnable> updates = new ArrayList<>();
    Map<String, ModuleProgress> others = new LinkedHashMap<>();
    SaveFile saved;
    try {
      saved = gson.fromJson(json, SaveFile.class);
      if (saved == null || saved.getModules() == null) {
        return;
      }
      others.putAll(saved.getModules());
      for (LabModule module : getModules()) {
        ModuleProgress progress = others.remove(module.getTitle());
        if (progress != null) {
          updates.addAll(updatesFor(module, progress));
        }
      }
    } catch (RuntimeException e) {
      // Gson, date-parsing and null errors all mean the file isn't a valid save.
      moveAside();
      return;
    }
    updates.forEach(Runnable::run);
    otherModules.putAll(others);
    if (saved.getLab() == null) {
      return;
    }
    // Read the lab and every snapshot first, so a bad one leaves the starting lab as it is.
    try {
      Class<? extends Lab> labClass = getLab().getClass();
      Lab savedLab = readLab(saved.getLab(), labClass);
      List<Snapshot> history = saved.getSnapshots() == null ? List.of() : saved.getSnapshots();
      for (Snapshot snapshot : history) {
        if (snapshot.getStepStart() < UNDO_POINT) {
          throw new JsonParseException("Snapshot has no step: " + snapshot.getStepStart());
        }
        readLab(snapshot.getJson(), labClass);
      }
      restoreHistory(savedLab, history);
    } catch (RuntimeException e) {
      // Only the lab is lost; the quiz and notebook stay. Keep a copy of the file to look at.
      copyAside();
    }
  }

  /** Reads a saved lab, throwing if the text is bad or missing. */
  private Lab readLab(String json, Class<? extends Lab> labClass) {
    Lab lab = labJson.fromJson(json, labClass);
    if (lab == null) {
      throw new JsonParseException("No lab saved");
    }
    return lab;
  }

  /** The changes that put a module's saved progress back, each value already checked. */
  private static List<Runnable> updatesFor(LabModule module, ModuleProgress progress) {
    List<Runnable> updates = new ArrayList<>();
    if (progress.getSavedAnswers() != null) {
      List<Set<Integer>> answers = new ArrayList<>();
      for (Set<Integer> answer : progress.getSavedAnswers()) {
        // Throws on a null answer or choice.
        answers.add(Set.copyOf(answer));
      }
      // A different number of answers means the questions changed since the save.
      if (answers.size() == module.getPreProcedureQuestions().size()) {
        updates.add(() -> module.setSavedAnswers(answers));
      }
    }
    if (progress.getLastEdited() != null) {
      LocalDateTime lastEdited = LocalDateTime.parse(progress.getLastEdited());
      updates.add(() -> module.setLastEdited(lastEdited));
    }
    if (progress.getNotebook() != null) {
      String notebook = progress.getNotebook();
      updates.add(() -> module.setNotebook(notebook));
    }
    return updates;
  }

  private void moveAside() {
    try {
      Files.move(file, brokenFile(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Couldn't move aside unreadable " + file, e);
    }
  }

  private void copyAside() {
    try {
      Files.copy(file, brokenFile(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Couldn't copy aside unreadable lab in " + file, e);
    }
  }

  private Path brokenFile() {
    return file.resolveSibling(file.getFileName() + ".broken");
  }

  private void write() {
    Map<String, ModuleProgress> progress = new LinkedHashMap<>();
    for (LabModule module : getModules()) {
      progress.put(module.getTitle(), new ModuleProgress(module.getSavedAnswers(),
          module.getLastEdited().map(LocalDateTime::toString).orElse(null),
          module.getNotebook()));
    }
    otherModules.forEach(progress::putIfAbsent);
    Path temporary = null;
    try {
      Path parent = file.toAbsolutePath().getParent();
      Files.createDirectories(parent);
      // Write a temporary file first so a crash mid-write can't leave a half-written save.
      temporary = Files.createTempFile(parent, "save", ".tmp");
      Files.writeString(temporary,
          gson.toJson(new SaveFile(progress, labJson.toJson(getLab()), getSnapshots())));
      Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.ATOMIC_MOVE);
    } catch (IOException e) {
      if (temporary != null) {
        try {
          Files.deleteIfExists(temporary);
        } catch (IOException cleanup) {
          e.addSuppressed(cleanup);
        }
      }
      throw new UncheckedIOException("Couldn't save to " + file, e);
    }
  }

  /**
   * The save file's contents: each module's progress, by module title, then the lab as
   * {@link LabJson} text and its undo history. Files from before the lab was saved have no lab.
   */
  private static final class SaveFile {
    private final Map<String, ModuleProgress> modules;
    private final String lab;
    private final List<Snapshot> snapshots;

    SaveFile(Map<String, ModuleProgress> modules, String lab, List<Snapshot> snapshots) {
      this.modules = modules;
      this.lab = lab;
      this.snapshots = snapshots;
    }

    Map<String, ModuleProgress> getModules() {
      return modules;
    }

    String getLab() {
      return lab;
    }

    List<Snapshot> getSnapshots() {
      return snapshots;
    }
  }

  /**
   * One module's saved answers, when it was last edited (ISO text, null if never) and its
   * notebook (HTML).
   */
  private static final class ModuleProgress {
    private final List<Set<Integer>> savedAnswers;
    private final String lastEdited;
    private final String notebook;

    ModuleProgress(List<Set<Integer>> savedAnswers, String lastEdited, String notebook) {
      this.savedAnswers = savedAnswers;
      this.lastEdited = lastEdited;
      this.notebook = notebook;
    }

    List<Set<Integer>> getSavedAnswers() {
      return savedAnswers;
    }

    String getLastEdited() {
      return lastEdited;
    }

    String getNotebook() {
      return notebook;
    }
  }
}
