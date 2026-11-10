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
    private final List<String> learningObjectives;
    private final boolean completed;
    private final LocalDateTime lastEdited;
    private final List<QuestionInfo> questions;

    /**
     * Creates the module info.
     *
     * @param title display name
     * @param dueDate submission deadline
     * @param overview short description of the pre-lab
     * @param learningObjectives what the student should be able to do afterwards, in order
     * @param completed whether the student has finished it
     * @param lastEdited when the student last worked on it, or null if never
     * @param questions the pre-procedure quiz, without its answers
     */
    public ModuleInfo(String title, LocalDate dueDate, String overview,
        List<String> learningObjectives, boolean completed, LocalDateTime lastEdited,
        List<QuestionInfo> questions) {
      this.title = title;
      this.dueDate = dueDate;
      this.overview = overview;
      this.learningObjectives = learningObjectives;
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

    public List<String> getLearningObjectives() {
      return learningObjectives;
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
    private final boolean multipleChoice;
    private final boolean imageChoices;
    private final boolean equation;
    private final String reactants;
    private final int blankCount;
    private final List<String> choices;

    /**
     * Creates the question info.
     *
     * @param prompt the question text
     * @param multipleChoice true if the student ticks every correct choice, false if they
     *     pick one
     * @param imageChoices true if each choice is an image resource path, not a text label
     * @param equation true if the student fills product tiles into blanks after the reactants
     * @param reactants the left side of the equation, or empty when not {@code equation}
     * @param blankCount how many blanks the equation has, or 0 when not {@code equation}
     * @param choices choice labels, image resource paths when {@code imageChoices}, or
     *     product tiles when {@code equation}
     */
    public QuestionInfo(String prompt, boolean multipleChoice, boolean imageChoices,
        boolean equation, String reactants, int blankCount, List<String> choices) {
      this.prompt = prompt;
      this.multipleChoice = multipleChoice;
      this.imageChoices = imageChoices;
      this.equation = equation;
      this.reactants = reactants;
      this.blankCount = blankCount;
      this.choices = choices;
    }

    public String getPrompt() {
      return prompt;
    }

    public boolean isMultipleChoice() {
      return multipleChoice;
    }

    public boolean isImageChoices() {
      return imageChoices;
    }

    public boolean isEquation() {
      return equation;
    }

    public String getReactants() {
      return reactants;
    }

    public int getBlankCount() {
      return blankCount;
    }

    public List<String> getChoices() {
      return choices;
    }
  }
}
