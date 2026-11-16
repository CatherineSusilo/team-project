package usecase.answercheckpoint;

import java.util.Set;

/**
 * The student's answer to a checkpoint question.
 */
public final class AnswerCheckpointInputData {
  private final int stepIndex;
  private final Set<Integer> selected;

  /**
   * Creates the input.
   *
   * @param stepIndex the index of the step the checkpoint comes after
   * @param selected the indexes of the choices the student selected
   */
  public AnswerCheckpointInputData(int stepIndex, Set<Integer> selected) {
    this.stepIndex = stepIndex;
    this.selected = selected;
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public Set<Integer> getSelected() {
    return selected;
  }
}
