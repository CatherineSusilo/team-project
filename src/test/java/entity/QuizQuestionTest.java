package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.QuizQuestion.Type;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuizQuestionTest {
  private static final List<String> CHOICES = List.of("a", "b", "c", "d");

  @Test
  void tickBoxAnswerMustMatchExactly() {
    QuizQuestion question = new QuizQuestion("?", Type.MULTIPLE, CHOICES, Set.of(0, 2), "");
    assertTrue(question.isCorrect(Set.of(0, 2)));
    assertFalse(question.isCorrect(Set.of(0)), "missing a correct choice");
    assertFalse(question.isCorrect(Set.of(0, 1, 2)), "extra wrong choice");
    assertFalse(question.isCorrect(Set.of()), "nothing selected");
  }

  @Test
  void singleAnswerChecksTheOneChoice() {
    QuizQuestion question = new QuizQuestion("?", Type.SINGLE, CHOICES, Set.of(1), "");
    assertTrue(question.isCorrect(Set.of(1)));
    assertFalse(question.isCorrect(Set.of(3)));
  }

  @Test
  void equationAcceptsTheProductsInAnyOrder() {
    QuizQuestion question = new QuizQuestion("?", "A + B", CHOICES, Set.of(0, 1), "");
    assertEquals(Type.EQUATION, question.getType());
    assertEquals("A + B", question.getReactants());
    assertEquals(2, question.getBlankCount());
    assertTrue(question.isCorrect(Set.of(1, 0)));
    assertFalse(question.isCorrect(Set.of(0)), "one blank empty");
    assertFalse(question.isCorrect(Set.of(0, 2)), "wrong product");
  }

  @Test
  void rejectsBadEquations() {
    assertThrows(IllegalArgumentException.class,
        () -> new QuizQuestion("?", Type.EQUATION, CHOICES, Set.of(0), ""));
    assertThrows(IllegalArgumentException.class,
        () -> new QuizQuestion("?", "A + B", CHOICES, Set.of(), ""));
  }

  @Test
  void rejectsBadAnswerKeys() {
    assertThrows(IllegalArgumentException.class,
        () -> new QuizQuestion("?", Type.SINGLE, CHOICES, Set.of(4), ""));
    assertThrows(IllegalArgumentException.class,
        () -> new QuizQuestion("?", Type.SINGLE, CHOICES, Set.of(0, 1), ""));
  }
}
