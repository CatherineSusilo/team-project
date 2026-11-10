package adapter.viewmodel;

import java.util.List;

/**
 * One module as the dashboard shows it. Every field is ready to display.
 */
public final class ModuleState {
  private final String title;
  private final String dueDate;
  private final String status;
  private final String lastModified;
  private final boolean started;
  private final String overview;
  private final List<QuestionState> questions;

  /**
   * Creates the module state.
   *
   * @param title display name
   * @param dueDate submission deadline
   * @param status "Completed" or "To do"
   * @param lastModified when it was last edited, or "NOT STARTED"
   * @param started whether the student has worked on it, which shows Continue
   * @param overview short description of the pre-lab
   * @param questions the pre-procedure quiz
   */
  public ModuleState(String title, String dueDate, String status, String lastModified,
      boolean started, String overview, List<QuestionState> questions) {
    this.title = title;
    this.dueDate = dueDate;
    this.status = status;
    this.lastModified = lastModified;
    this.started = started;
    this.overview = overview;
    this.questions = questions;
  }

  public String getTitle() {
    return title;
  }

  public String getDueDate() {
    return dueDate;
  }

  public String getStatus() {
    return status;
  }

  public String getLastModified() {
    return lastModified;
  }

  public boolean isStarted() {
    return started;
  }

  public String getOverview() {
    return overview;
  }

  public List<QuestionState> getQuestions() {
    return questions;
  }
}
