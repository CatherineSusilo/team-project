package entity;

/**
 * A question the student answers after finishing one procedure step, before moving on.
 */
public class Checkpoint {
  private final int stepIndex;
  private final String stepTitle;
  private final QuizQuestion question;
  private final String takeaway;

  /**
   * Creates a checkpoint.
   *
   * @param stepIndex the index of the step it comes after, matching the Lab's current index
   * @param stepTitle the name of that step, e.g. "Mix"
   * @param question what to ask; a single or tick-all text question
   * @param takeaway one sentence to remember, shown once the student answers correctly
   * @throws IllegalArgumentException if the question is an image or equation question
   */
  public Checkpoint(int stepIndex, String stepTitle, QuizQuestion question, String takeaway) {
    if (question.getType() == QuizQuestion.Type.IMAGE
        || question.getType() == QuizQuestion.Type.EQUATION) {
      throw new IllegalArgumentException("Checkpoints only support text choice questions");
    }
    this.stepIndex = stepIndex;
    this.stepTitle = stepTitle;
    this.question = question;
    this.takeaway = takeaway;
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public String getStepTitle() {
    return stepTitle;
  }

  public QuizQuestion getQuestion() {
    return question;
  }

  public String getTakeaway() {
    return takeaway;
  }
}
