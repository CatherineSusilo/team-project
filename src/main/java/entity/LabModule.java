package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A pre-lab module a student works through.
 */
public class LabModule {
  private final String title;
  private final LocalDate dueDate;
  private final String overview;
  private final boolean completed;
  private LocalDateTime lastEdited;
  private final List<QuizQuestion> preProcedureQuestions;

  /**
   * Creates a module.
   *
   * @param title display name, e.g. "Pre-lab 3"
   * @param dueDate submission deadline
   * @param overview short description of what the pre-lab covers
   * @param completed whether the student has finished it
   * @param lastEdited when the student last worked on it, or null if never
   * @param preProcedureQuestions quiz the student answers before the walkthrough
   */
  public LabModule(String title, LocalDate dueDate, String overview, boolean completed,
      LocalDateTime lastEdited, List<QuizQuestion> preProcedureQuestions) {
    this.title = title;
    this.dueDate = dueDate;
    this.overview = overview;
    this.completed = completed;
    this.lastEdited = lastEdited;
    this.preProcedureQuestions = List.copyOf(preProcedureQuestions);
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

  public void setLastEdited(LocalDateTime lastEdited) {
    this.lastEdited = lastEdited;
  }

  public List<QuizQuestion> getPreProcedureQuestions() {
    return preProcedureQuestions;
  }
}
