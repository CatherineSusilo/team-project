package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.LabModule;
import entity.LabStep;
import entity.QuizQuestion;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class PreLab3QuizTest {

  @Test
  void everyStructureImageExists() {
    // Building the questions also validates every answer key.
    for (QuizQuestion question : PreLab3Quiz.questions()) {
      if (question.getType() == QuizQuestion.Type.IMAGE) {
        for (String path : question.getChoices()) {
          assertNotNull(getClass().getResource(path), "missing " + path);
        }
      }
    }
  }

  @Test
  void partsMatchTheLabSteps() {
    LabModule module = PreLab3Quiz.module();
    List<LabStep> steps = Lab3Definition.steps();
    assertEquals(List.of("Part A", "Part B", "IR Spectrum (optional)"), module.getPartTitles());
    // With no quiz answers, one finished required step always lifts the module above 0
    // (A: 11 / 3, B: 14 / 3); a finished optional step never does.
    for (int i = 0; i < steps.size(); i++) {
      module.setCompletedSteps(Set.of(i));
      assertEquals(steps.get(i).isOptional(), module.getProgress() == 0, "step " + i);
    }
    module.setCompletedSteps(
        IntStream.range(0, steps.size()).boxed().collect(Collectors.toSet()));
    assertEquals(List.of(100, 100, 100), module.getPartProgress(), "the parts cover every step");
  }
}
