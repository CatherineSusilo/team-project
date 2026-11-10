package usecase.loaddashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Every module on the dashboard.
 *
 * @param modules the modules, in display order
 */
public record LoadDashboardOutputData(List<ModuleInfo> modules) {

  /**
   * One module.
   *
   * @param title display name
   * @param dueDate submission deadline
   * @param overview short description of the pre-lab
   * @param completed whether the student has finished it
   * @param lastEdited when the student last worked on it, or null if never
   * @param questions the pre-procedure quiz, without its answers
   */
  public record ModuleInfo(String title, LocalDate dueDate, String overview, boolean completed,
      LocalDateTime lastEdited, List<QuestionInfo> questions) {
  }

  /**
   * One quiz question, without its answer.
   *
   * @param prompt the question text
   * @param type SINGLE, MULTIPLE or IMAGE
   * @param choices choice labels, or image resource paths for IMAGE questions
   */
  public record QuestionInfo(String prompt, String type, List<String> choices) {
  }
}
