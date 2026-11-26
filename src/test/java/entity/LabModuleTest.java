package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class LabModuleTest {
  private static final List<QuizQuestion> QUESTIONS = List.of(
      new QuizQuestion("q1", Type.SINGLE, List.of("a", "b"), Set.of(0), ""),
      new QuizQuestion("q2", Type.SINGLE, List.of("a", "b"), Set.of(1), ""),
      new QuizQuestion("q3", Type.SINGLE, List.of("a", "b"), Set.of(1), ""));
  private static final List<String> LAB_PARTS =
      List.of("Part A", "Part B", "IR Spectrum (optional)");

  private final LabModule module = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), List.of("Part A", "Part B"), null, QUESTIONS);
  // Part A = steps 0-8, Part B = 9-15, IR = 16 (optional).
  private final LabModule labModule = new LabModule("Pre-lab", LocalDate.of(2026, 11, 30), "",
      List.of(), LAB_PARTS, null, QUESTIONS, List.of(9, 7, 1), Set.of(2));

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

  @Test
  void clearProgressForgetsAnswersNotebookAndLastEdited() {
    module.setSavedAnswers(List.of(Set.of(0), Set.of(1), Set.of(1)));
    module.setNotebook("<b>notes</b>");
    module.setLastEdited(LocalDateTime.of(2026, 11, 26, 9, 0));
    module.clearProgress();
    assertEquals(List.of(), module.getSavedAnswers());
    assertEquals("", module.getNotebook());
    assertTrue(module.getLastEdited().isEmpty(), "back to not started");
    assertEquals(0, module.getQuizProgress());
  }

  @Test
  void partProgressCountsStepsInEachPartsRange() {
    labModule.setCompletedSteps(steps(0, 8));
    assertEquals(List.of(100, 0, 0), labModule.getPartProgress());
    labModule.setCompletedSteps(Set.of(0, 1, 2, 9, 16));
    assertEquals(List.of(33, 14, 100), labModule.getPartProgress());
  }

  @Test
  void optionalPartDoesNotHoldBackTheModule() {
    labModule.setSavedAnswers(List.of(Set.of(0), Set.of(1), Set.of(1)));
    labModule.setCompletedSteps(steps(0, 15));
    assertEquals(List.of(100, 100, 0), labModule.getPartProgress());
    assertEquals(100, labModule.getProgress(), "IR skipped, the module is still complete");
    labModule.setCompletedSteps(steps(0, 16));
    assertEquals(100, labModule.getProgress());
  }

  @Test
  void optionalPartIsNotAveragedIn() {
    Set<Integer> partAAndIr = new HashSet<>(steps(0, 8));
    partAAndIr.add(16);
    labModule.setCompletedSteps(partAAndIr);
    assertEquals(33, labModule.getProgress(), "(quiz 0 + A 100 + B 0) / 3");
  }

  @Test
  void stepsOutsideEveryPartAreIgnored() {
    labModule.setCompletedSteps(Set.of(17, 40));
    assertEquals(List.of(0, 0, 0), labModule.getPartProgress());
  }

  @Test
  void completedStepsAreCopied() {
    Set<Integer> done = new HashSet<>(Set.of(0));
    labModule.setCompletedSteps(done);
    done.addAll(steps(1, 8));
    assertEquals(11, labModule.getPartProgress().get(0));
  }

  @Test
  void oldConstructorPartsStayAtZero() {
    module.setCompletedSteps(Set.of(0, 1, 9));
    assertEquals(List.of(0, 0), module.getPartProgress());
  }

  @Test
  void partStepCountsNeedOnePerPart() {
    assertThrows(IllegalArgumentException.class, () -> new LabModule("Pre-lab",
        LocalDate.of(2026, 11, 30), "", List.of(), LAB_PARTS, null, QUESTIONS, List.of(9, 7),
        Set.of()));
  }

  @Test
  void optionalPartsMustBeParts() {
    assertThrows(IllegalArgumentException.class, () -> new LabModule("Pre-lab",
        LocalDate.of(2026, 11, 30), "", List.of(), LAB_PARTS, null, QUESTIONS, List.of(9, 7, 1),
        Set.of(3)));
  }

  /** Every step index from {@code first} to {@code last}, both included. */
  private static Set<Integer> steps(int first, int last) {
    return IntStream.rangeClosed(first, last).boxed().collect(Collectors.toSet());
  }
}
