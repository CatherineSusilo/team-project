package entity;

import java.util.List;
import java.util.Set;

/**
 * One quiz question with its choices, the correct answer(s), and an explanation.
 */
public class QuizQuestion {

  /**
   * How the student answers the question.
   */
  public enum Type {
    /** Pick one text choice. */
    SINGLE,
    /** Tick every correct text choice. */
    MULTIPLE,
    /** Pick one image; each choice is an image resource path like "/structures/x.png". */
    IMAGE
  }

  private final String prompt;
  private final Type type;
  private final List<String> choices;
  private final Set<Integer> correctChoices;
  private final String explanation;

  /**
   * Creates a question.
   *
   * @param prompt the question text
   * @param type how the student answers
   * @param choices choice labels, or image resource paths for {@link Type#IMAGE}
   * @param correctChoices indexes into {@code choices} of every correct answer
   * @param explanation shown after the student checks their answer
   * @throws IllegalArgumentException if an index is out of range, or a single-answer
   *     question doesn't have exactly one correct choice
   */
  public QuizQuestion(String prompt, Type type, List<String> choices,
      Set<Integer> correctChoices, String explanation) {
    for (int index : correctChoices) {
      if (index < 0 || index >= choices.size()) {
        throw new IllegalArgumentException("Correct choice " + index + " is out of range");
      }
    }
    if (type != Type.MULTIPLE && correctChoices.size() != 1) {
      throw new IllegalArgumentException(type + " questions need exactly one correct choice");
    }
    this.prompt = prompt;
    this.type = type;
    this.choices = List.copyOf(choices);
    this.correctChoices = Set.copyOf(correctChoices);
    this.explanation = explanation;
  }

  /**
   * Checks an answer. For tick-box questions every correct choice, and nothing else,
   * must be selected.
   *
   * @param selected indexes of the choices the student selected
   * @return whether the answer is fully correct
   */
  public boolean isCorrect(Set<Integer> selected) {
    return correctChoices.equals(selected);
  }

  public String getPrompt() {
    return prompt;
  }

  public Type getType() {
    return type;
  }

  public List<String> getChoices() {
    return choices;
  }

  public String getExplanation() {
    return explanation;
  }
}
