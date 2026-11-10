package adapter.viewmodel;

import java.util.List;

/**
 * One quiz question as the quiz page shows it.
 */
public final class QuestionState {
  private final String prompt;
  private final String type;
  private final List<String> choices;

  /**
   * Creates the question state.
   *
   * @param prompt the question text
   * @param type SINGLE, MULTIPLE or IMAGE
   * @param choices choice labels, or image resource paths for IMAGE questions
   */
  public QuestionState(String prompt, String type, List<String> choices) {
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
