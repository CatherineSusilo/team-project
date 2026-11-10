package adapter.viewmodel;

import java.util.List;

/**
 * One quiz question as the quiz page shows it.
 */
public final class QuestionState {
  private final String prompt;
  private final boolean multipleChoice;
  private final boolean imageChoices;
  private final List<String> choices;

  /**
   * Creates the question state.
   *
   * @param prompt the question text
   * @param multipleChoice true if the student ticks every correct choice, false if they
   *     pick one
   * @param imageChoices true if each choice is an image resource path, not a text label
   * @param choices choice labels, or image resource paths when {@code imageChoices}
   */
  public QuestionState(String prompt, boolean multipleChoice, boolean imageChoices,
      List<String> choices) {
    this.prompt = prompt;
    this.multipleChoice = multipleChoice;
    this.imageChoices = imageChoices;
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

  public List<String> getChoices() {
    return choices;
  }
}
