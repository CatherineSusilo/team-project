package app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.QuizQuestion;
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
}
