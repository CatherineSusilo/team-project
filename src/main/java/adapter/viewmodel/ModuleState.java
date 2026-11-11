package adapter.viewmodel;

import java.util.List;
import java.util.Set;

/**
 * One module as the dashboard shows it. Every field is ready to display.
 */
public final class ModuleState {
  private final String title;
  private final String dueDate;
  private final String progress;
  private final boolean complete;
  private final String overview;
  private final List<String> learningObjectives;
  private final List<PartState> parts;
  private final List<Set<Integer>> savedAnswers;
  private final List<QuestionState> questions;

  /**
   * Creates the module state.
   *
   * @param title display name
   * @param dueDate submission deadline
   * @param progress "Complete", or how much is done, e.g. "33%"
   * @param complete whether the whole module is done, which enables Export
   * @param overview short description of the pre-lab
   * @param learningObjectives what the student should be able to do afterwards, in order
   * @param parts the progress rows: the quiz first, then each lab part
   * @param savedAnswers the student's saved choice indexes per question, or empty if none
   * @param questions the pre-procedure quiz
   */
  public ModuleState(String title, String dueDate, String progress, boolean complete,
      String overview, List<String> learningObjectives, List<PartState> parts,
      List<Set<Integer>> savedAnswers, List<QuestionState> questions) {
    this.title = title;
    this.dueDate = dueDate;
    this.progress = progress;
    this.complete = complete;
    this.overview = overview;
    this.learningObjectives = learningObjectives;
    this.parts = parts;
    this.savedAnswers = savedAnswers;
    this.questions = questions;
  }

  public String getTitle() {
    return title;
  }

  public String getDueDate() {
    return dueDate;
  }

  public String getProgress() {
    return progress;
  }

  public boolean isComplete() {
    return complete;
  }

  public String getOverview() {
    return overview;
  }

  public List<String> getLearningObjectives() {
    return learningObjectives;
  }

  public List<PartState> getParts() {
    return parts;
  }

  public List<Set<Integer>> getSavedAnswers() {
    return savedAnswers;
  }

  public List<QuestionState> getQuestions() {
    return questions;
  }
}
