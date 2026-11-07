package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A pre-lab module a student works through.
 */
public class LabModule {
  private final String title;
  private final LocalDate dueDate;
  private final String overview;
  private final boolean completed;
  private final LocalDateTime lastEdited;

  /**
   * Creates a module.
   *
   * @param title display name, e.g. "Pre-lab 3"
   * @param dueDate submission deadline
   * @param overview short description of what the pre-lab covers
   * @param completed whether the student has finished it
   * @param lastEdited when the student last worked on it, or null if never
   */
  public LabModule(String title, LocalDate dueDate, String overview, boolean completed,
      LocalDateTime lastEdited) {
    this.title = title;
    this.dueDate = dueDate;
    this.overview = overview;
    this.completed = completed;
    this.lastEdited = lastEdited;
  }

  public String getTitle() {
    return title;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public String getOverview() {
    return overview;
  }

  public boolean isCompleted() {
    return completed;
  }

  public Optional<LocalDateTime> getLastEdited() {
    return Optional.ofNullable(lastEdited);
  }
}
