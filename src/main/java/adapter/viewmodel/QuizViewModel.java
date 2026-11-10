package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * What the quiz page shows after a check. The CheckQuiz presenter writes it; the quiz reads it.
 */
public class QuizViewModel {
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private List<FeedbackState> feedback = List.of();
  private boolean continueVisible;
  private boolean submitted;
  // TODO: save quiz progress: add boolean saved, set by a SaveQuiz presenter once partial
  //  answers are stored, so the quiz page knows it can go back to the dashboard.
  private String message = "";

  /**
   * Replaces everything the quiz page shows.
   *
   * @param feedback feedback for each question, in order
   * @param continueVisible whether to show Continue
   * @param submitted whether progress was saved, so the page should move on
   * @param message an error to show, or empty for none
   */
  public void setState(List<FeedbackState> feedback, boolean continueVisible, boolean submitted,
      String message) {
    this.feedback = List.copyOf(feedback);
    this.continueVisible = continueVisible;
    this.submitted = submitted;
    this.message = message;
  }

  /** Tells the quiz page to redraw. */
  public void firePropertyChanged() {
    support.firePropertyChange("state", null, this);
  }

  /**
   * Registers a view to redraw when the state changes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public List<FeedbackState> getFeedback() {
    return feedback;
  }

  public boolean isContinueVisible() {
    return continueVisible;
  }

  public boolean isSubmitted() {
    return submitted;
  }

  public String getMessage() {
    return message;
  }
}
