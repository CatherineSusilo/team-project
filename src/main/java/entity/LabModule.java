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
  private final List<String> learningObjectives;
  private final boolean completed;
  private LocalDateTime lastEdited;
  private final List<QuizQuestion> preProcedureQuestions;
  // TODO: save quiz progress: add List<Set<Integer>> savedAnswers (one set per question,
  //  empty = unanswered) with a getter/setter, and an int getQuizProgress() that returns the
  //  percent of saved answers that QuizQuestion.isCorrect accepts.

  /**
   * Creates a module.
   *
   * @param title display name, e.g. "Pre-lab 3"
   * @param dueDate submission deadline
   * @param overview short description of what the pre-lab covers
   * @param learningObjectives what the student should be able to do afterwards, in order
   * @param completed whether the student has finished it
   * @param lastEdited when the student last worked on it, or null if never
   * @param preProcedureQuestions quiz the student answers before the walkthrough
   */
  public LabModule(String title, LocalDate dueDate, String overview,
      List<String> learningObjectives, boolean completed, LocalDateTime lastEdited,
      List<QuizQuestion> preProcedureQuestions) {
    this.title = title;
    this.dueDate = dueDate;
    this.overview = overview;
    this.learningObjectives = List.copyOf(learningObjectives);
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

  public List<String> getLearningObjectives() {
    return learningObjectives;
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
