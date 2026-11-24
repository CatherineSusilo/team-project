package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * (each module's answers, notebook and when it was last edited); the module content stays in
 * code.
 */
public class JsonLabDataAccessObject extends InMemoryLabDataAccessObject {
  // TODO: also save the lab and its undo snapshots, with data.LabJson, once Lab has fields.
  private final Path file;
  // No HTML escaping, so notebooks read as written ("<b>", not "\u003cb\u003e").
  private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
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
   * and the student starts fresh, so the next save doesn't overwrite it.
   *
   * @param file the save file; it is created on the first save
   * @param modules the modules assigned to the student
   */
  public JsonLabDataAccessObject(Path file, List<LabModule> modules) {
    super(modules);
    this.file = file;
    load();
  }

  @Override
  public void saveModule(LabModule module) {
    super.saveModule(module);
    write();
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
    try {
      SaveFile saved = gson.fromJson(json, SaveFile.class);
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
      Files.move(file, file.resolveSibling(file.getFileName() + ".broken"),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Couldn't move aside unreadable " + file, e);
    }
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
      Files.writeString(temporary, gson.toJson(new SaveFile(progress)));
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

  /** The save file's contents: each module's progress, by module title. */
  private static final class SaveFile {
    private final Map<String, ModuleProgress> modules;

    SaveFile(Map<String, ModuleProgress> modules) {
      this.modules = modules;
    }

    Map<String, ModuleProgress> getModules() {
      return modules;
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
