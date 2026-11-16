package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * What the checkpoint page shows. The ShowCheckpoint presenter writes the question; the
 * AnswerCheckpoint presenter writes the feedback and whether the student may continue.
 */
public class CheckpointViewModel {
  /** Fired when a new question is loaded. */
  public static final String QUESTION = "question";
  /** Fired when the feedback or message changes. */
  public static final String FEEDBACK = "feedback";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private int stepIndex = -1;
  private String stepTitle = "";
  private String prompt = "";
  private List<String> choices = List.of();
  private boolean multipleChoice;
  private boolean correct;
  private String verdict = "";
  private String explanation = "";
  private String takeaway = "";
  private String message = "";

  /**
   * Replaces the question and clears any feedback from the last one.
   *
   * @param stepIndex the step the checkpoint comes after
   * @param stepTitle that step's name
   * @param prompt the question text
   * @param choices the choice labels
   * @param multipleChoice true if the student ticks every correct choice
   */
  public void setQuestion(int stepIndex, String stepTitle, String prompt, List<String> choices,
      boolean multipleChoice) {
    this.stepIndex = stepIndex;
    this.stepTitle = stepTitle;
    this.prompt = prompt;
    this.choices = List.copyOf(choices);
    this.multipleChoice = multipleChoice;
    setFeedback(false, "", "", "", "");
  }

  /**
   * Replaces the feedback under the question.
   *
   * @param correct whether the answer was right, which lets the student continue
   * @param verdict e.g. "Correct." or "Not quite."; empty for none
   * @param explanation why the answer is right; empty unless correct
   * @param takeaway the one thing to remember; empty unless correct
   * @param message an error to show, or empty for none
   */
  public void setFeedback(boolean correct, String verdict, String explanation, String takeaway,
      String message) {
    this.correct = correct;
    this.verdict = verdict;
    this.explanation = explanation;
    this.takeaway = takeaway;
    this.message = message;
  }

  /**
   * Tells the checkpoint page to redraw.
   *
   * @param property {@link #QUESTION} or {@link #FEEDBACK}
   */
  public void firePropertyChanged(String property) {
    support.firePropertyChange(property, null, this);
  }

  /**
   * Registers a view to redraw when the state changes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public String getStepTitle() {
    return stepTitle;
  }

  public String getPrompt() {
    return prompt;
  }

  public List<String> getChoices() {
    return choices;
  }

  public boolean isMultipleChoice() {
    return multipleChoice;
  }

  /** Whether the student may continue: they answered the checkpoint correctly. */
  public boolean isCorrect() {
    return correct;
  }

  public String getVerdict() {
    return verdict;
  }

  public String getExplanation() {
    return explanation;
  }

  public String getTakeaway() {
    return takeaway;
  }

  public String getMessage() {
    return message;
  }
}
