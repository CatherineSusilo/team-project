package usecase.loaddashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Every module on the dashboard.
 */
public final class LoadDashboardOutputData {
  private final List<ModuleInfo> modules;

  /**
   * Creates the output.
   *
   * @param modules the modules, in display order
   */
  public LoadDashboardOutputData(List<ModuleInfo> modules) {
    this.modules = modules;
  }

  public List<ModuleInfo> getModules() {
    return modules;
  }

  /**
   * One module.
   */
  public static final class ModuleInfo {
    private final String title;
    private final LocalDate dueDate;
    private final String overview;
    private final boolean completed;
    private final LocalDateTime lastEdited;
    private final List<QuestionInfo> questions;

    /**
     * Creates the module info.
     *
     * @param title display name
     * @param dueDate submission deadline
     * @param overview short description of the pre-lab
     * @param completed whether the student has finished it
     * @param lastEdited when the student last worked on it, or null if never
     * @param questions the pre-procedure quiz, without its answers
     */
    public ModuleInfo(String title, LocalDate dueDate, String overview, boolean completed,
        LocalDateTime lastEdited, List<QuestionInfo> questions) {
      this.title = title;
      this.dueDate = dueDate;
      this.overview = overview;
      this.completed = completed;
      this.lastEdited = lastEdited;
      this.questions = questions;
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

    /** When the student last worked on it, or null if never. */
    public LocalDateTime getLastEdited() {
      return lastEdited;
    }

    public List<QuestionInfo> getQuestions() {
      return questions;
    }
  }

  /**
   * One quiz question, without its answer.
   */
  public static final class QuestionInfo {
    private final String prompt;
    private final String type;
    private final List<String> choices;

    /**
     * Creates the question info.
     *
     * @param prompt the question text
     * @param type SINGLE, MULTIPLE or IMAGE
     * @param choices choice labels, or image resource paths for IMAGE questions
     */
    public QuestionInfo(String prompt, String type, List<String> choices) {
      this.prompt = prompt;
      this.type = type;
      this.choices = choices;
    }

    public String getPrompt() {
      return prompt;
    }

    public String getType() {
      return type;
    }

    public List<String> getChoices() {
      return choices;
    }
  }
}
