package adapter.viewmodel;

import java.util.List;

/**
 * One quiz question as the quiz page shows it.
 */
public final class QuestionState {
  private final String prompt;
  private final boolean multipleChoice;
  private final boolean imageChoices;
  private final boolean equation;
  private final String reactants;
  private final int blankCount;
  private final List<String> choices;

  /**
   * Creates the question state.
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
  public QuestionState(String prompt, boolean multipleChoice, boolean imageChoices,
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
