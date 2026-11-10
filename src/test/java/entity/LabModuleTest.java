package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabModuleTest {
  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), List.of("Part A", "Part B"), null, List.of(
          new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
          new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), ""),
          new QuizQuestion("q3", Type.SINGLE, List.of("a", "b"), Set.of(1), "")));

  @Test
  void nothingSavedIsNoProgress() {
    assertEquals(0, module.getQuizProgress());
    assertEquals(List.of(0, 0), module.getPartProgress());
    assertEquals(0, module.getProgress());
  }

  @Test
  void quizProgressCountsCorrectSavedAnswers() {
    module.setSavedAnswers(List.of(Set.of(0), Set.of(0), Set.of()));
    assertEquals(33, module.getQuizProgress(), "1 of 3 right; wrong and blank don't count");
    module.setSavedAnswers(List.of(Set.of(0), Set.of(1), Set.of(1)));
    assertEquals(100, module.getQuizProgress());
    assertEquals(33, module.getProgress(), "quiz done, Part A and Part B not started");
  }

  @Test
  void savedAnswersNeedOnePerQuestion() {
    assertThrows(IllegalArgumentException.class,
        () -> module.setSavedAnswers(List.of(Set.of(0))));
  }
}
