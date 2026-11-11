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
    IMAGE,
    /**
     * Complete an equation: the choices are product tiles, and the student fills one blank
     * per correct product, in any order.
     */
    EQUATION
  }

  private final String prompt;
  private final Type type;
  private final String reactants;
  private final List<String> choices;
  private final Set<Integer> correctChoices;
  private final String explanation;

  /**
   * Creates a choice question.
   *
   * @param prompt the question text
   * @param type how the student answers; not {@link Type#EQUATION}
   * @param choices choice labels, or image resource paths for {@link Type#IMAGE}
   * @param correctChoices indexes into {@code choices} of every correct answer
   * @param explanation shown after the student checks their answer
   * @throws IllegalArgumentException if {@code type} is EQUATION, an index is out of range,
   *     or a single-answer question doesn't have exactly one correct choice
   */
  public QuizQuestion(String prompt, Type type, List<String> choices,
      Set<Integer> correctChoices, String explanation) {
    this(prompt, type, "", choices, correctChoices, explanation);
    if (type == Type.EQUATION) {
      throw new IllegalArgumentException("EQUATION questions need their reactants");
    }
  }

  /**
   * Creates an equation question.
   *
   * @param prompt the question text
   * @param reactants the left side of the equation, e.g. "C₆H₅COOH + NaOH"
   * @param products the product tiles the student picks from
   * @param correctProducts indexes into {@code products} that belong in the blanks
   * @param explanation shown after the student checks their answer
   * @throws IllegalArgumentException if an index is out of range or there are no correct
   *     products
   */
  public QuizQuestion(String prompt, String reactants, List<String> products,
      Set<Integer> correctProducts, String explanation) {
    this(prompt, Type.EQUATION, reactants, products, correctProducts, explanation);
  }

  private QuizQuestion(String prompt, Type type, String reactants, List<String> choices,
      Set<Integer> correctChoices, String explanation) {
    for (int index : correctChoices) {
      if (index < 0 || index >= choices.size()) {
        throw new IllegalArgumentException("Correct choice " + index + " is out of range");
      }
    }
    if ((type == Type.SINGLE || type == Type.IMAGE) && correctChoices.size() != 1) {
      throw new IllegalArgumentException(type + " questions need exactly one correct choice");
    }
    if (type == Type.EQUATION && correctChoices.isEmpty()) {
      throw new IllegalArgumentException("EQUATION questions need at least one product");
    }
    this.prompt = prompt;
    this.type = type;
    this.reactants = reactants;
    this.choices = List.copyOf(choices);
    this.correctChoices = Set.copyOf(correctChoices);
    this.explanation = explanation;
  }

  /**
   * Checks an answer. For tick-box and equation questions every correct choice, and nothing
   * else, must be selected.
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

  /** The left side of the equation, or empty for choice questions. */
  public String getReactants() {
    return reactants;
  }

  /** How many blanks an equation question has: one per correct product. */
  public int getBlankCount() {
    return correctChoices.size();
  }

  public List<String> getChoices() {
    return choices;
  }

  public String getExplanation() {
    return explanation;
  }
}
