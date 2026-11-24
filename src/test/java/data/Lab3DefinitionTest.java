package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ApiType;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabStep;
import entity.StepStatus;
import entity.TemplateItem;
import entity.UnknownSample;
import entity.Vec2;
import java.util.List;
import org.junit.jupiter.api.Test;

class Lab3DefinitionTest {

  @Test
  void hasSeventeenStepsNumberedInOrderAndOnlyTheIrStepIsOptional() {
    List<LabStep> steps = Lab3Definition.steps();

    assertEquals(17, steps.size());
    for (int i = 0; i < steps.size(); i++) {
      assertEquals(i, steps.get(i).getId());
      assertEquals(i == 16, steps.get(i).isOptional());
    }
  }

  @Test
  void startsOnTheFirstStepWithTheUnknownVialOnTheBench() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));

    assertEquals(0, lab.getCurrentIndex());
    assertEquals(1, lab.getBench().countOf("unknown_vial"));
    assertTrue(lab.currentStep().getTitle().startsWith("A1"));
  }

  @Test
  void everyTrayTemplateExists() {
    for (LabStep step : Lab3Definition.steps()) {
      for (String templateId : step.getTrayTemplates()) {
        assertTrue(ItemTemplates.find(templateId).isPresent(), templateId);
      }
    }
  }

  @Test
  void theSecondStepNeedsBothTubes() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    assertTrue(lab.advance());
    assertEquals(StepStatus.INCOMPLETE, lab.status());

    for (String templateId : new String[] {"conical_tube", "test_tube"}) {
      String id = lab.getBench().nextId(templateId);
      lab.getBench().addItem(new TemplateItem(id, ItemTemplates.find(templateId).orElseThrow(),
          new Vec2(0, 0)), new Vec2(10, 10));
    }

    assertEquals(StepStatus.COMPLETE, lab.status());
    assertFalse(lab.currentStep().getTrayTemplates().isEmpty());
  }
}
