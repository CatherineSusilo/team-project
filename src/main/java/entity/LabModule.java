package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A pre-lab module a student works through: the pre-procedure quiz, then the lab parts.
 */
public class LabModule {
  private final String title;
  private final LocalDate dueDate;
  private final String overview;
  private final List<String> learningObjectives;
  private final List<String> partTitles;
  private LocalDateTime lastEdited;
  private final List<QuizQuestion> preProcedureQuestions;
  private List<Set<Integer>> savedAnswers = List.of();

  /**
   * Creates a module with no saved quiz answers.
   *
   * @param title display name, e.g. "Pre-lab 3"
   * @param dueDate submission deadline
   * @param overview short description of what the pre-lab covers
   * @param learningObjectives what the student should be able to do afterwards, in order
   * @param partTitles the lab parts after the quiz, in order, e.g. "Part A"
   * @param lastEdited when the student last worked on it, or null if never
   * @param preProcedureQuestions quiz the student answers before the walkthrough
   */
  public LabModule(String title, LocalDate dueDate, String overview,
      List<String> learningObjectives, List<String> partTitles, LocalDateTime lastEdited,
      List<QuizQuestion> preProcedureQuestions) {
    this.title = title;
    this.dueDate = dueDate;
    this.overview = overview;
    this.learningObjectives = List.copyOf(learningObjectives);
    this.partTitles = List.copyOf(partTitles);
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

  public List<String> getPartTitles() {
    return partTitles;
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

  /**
   * The student's last saved quiz answers: one set of choice indexes per question, empty for
   * an unanswered question. Empty if nothing has been saved yet.
   *
   * @return the saved answers
   */
  public List<Set<Integer>> getSavedAnswers() {
    return savedAnswers;
  }

  /**
   * Saves the student's quiz answers, right or wrong.
   *
   * @param answers one set of choice indexes per question
   * @throws IllegalArgumentException if there isn't one answer per question
   */
  public void setSavedAnswers(List<Set<Integer>> answers) {
    if (answers.size() != preProcedureQuestions.size()) {
      throw new IllegalArgumentException("Expected " + preProcedureQuestions.size()
          + " answers but got " + answers.size());
    }
    List<Set<Integer>> copy = new ArrayList<>();
    for (Set<Integer> answer : answers) {
      copy.add(Set.copyOf(answer));
    }
    savedAnswers = List.copyOf(copy);
  }

  /**
   * How much of the quiz is done.
   *
   * @return the percent of questions whose saved answer is correct, rounded down; 0 if the
   *     quiz has no questions
   */
  public int getQuizProgress() {
    if (preProcedureQuestions.isEmpty()) {
      return 0;
    }
    int correct = 0;
    for (int i = 0; i < savedAnswers.size(); i++) {
      if (preProcedureQuestions.get(i).isCorrect(savedAnswers.get(i))) {
        correct++;
      }
    }
    return correct * 100 / preProcedureQuestions.size();
  }

  /**
   * How much of each lab part is done, in the order of {@link #getPartTitles()}.
   *
   * @return one percent per part
   */
  public List<Integer> getPartProgress() {
    // TODO: work out each part's progress from the Lab's completed steps once they exist.
    return Collections.nCopies(partTitles.size(), 0);
  }

  /**
   * How much of the whole module is done: the average of the quiz and every part.
   *
   * @return a percent from 0 to 100, rounded down; 100 means complete
   */
  public int getProgress() {
    int total = getQuizProgress();
    for (int part : getPartProgress()) {
      total += part;
    }
    return total / (1 + partTitles.size());
  }
}
