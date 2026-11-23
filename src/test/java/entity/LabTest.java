package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LabTest {

  private static CheckStep needing(int id, String templateId) {
    return new CheckStep(id, "Step " + id, "Do step " + id, "Hint " + id, List.of(), false,
        lab -> lab.getBench().countOf(templateId) > 0, action -> true);
  }

  private static void put(Lab lab, String templateId) {
    String id = lab.getBench().nextId(templateId);
    lab.getBench().addItem(new TemplateItem(id, ItemTemplates.find(templateId).orElseThrow(),
        new Vec2(0, 0)), new Vec2(5, 5));
    lab.record(Action.spawn(id));
  }

  private static Lab twoStepLab() {
    return new Lab(List.of(needing(0, "beaker"), needing(1, "scale")), new Bench());
  }

  @Test
  void startsOnTheFirstStepIncompleteWithItsHint() {
    Lab lab = twoStepLab();

    assertEquals(0, lab.getCurrentIndex());
    assertEquals(StepStatus.INCOMPLETE, lab.status());
    assertEquals("Hint 0", lab.message());
    assertEquals(2, lab.getStepCount());
  }

  @Test
  void cannotAdvanceUntilTheStepIsComplete() {
    Lab lab = twoStepLab();

    assertFalse(lab.advance());
    put(lab, "beaker");

    assertEquals(StepStatus.COMPLETE, lab.status());
    assertTrue(lab.advance());
    assertEquals(1, lab.getCurrentIndex());
    assertEquals(StepStatus.INCOMPLETE, lab.status());
  }

  @Test
  void aCompletedStepStaysCompleteWhenTheBenchChanges() {
    Lab lab = twoStepLab();
    put(lab, "beaker");
    lab.getBench().removeItem("beaker-1");

    assertEquals(StepStatus.COMPLETE, lab.status());
  }

  @Test
  void cannotAdvancePastTheLastStep() {
    Lab lab = twoStepLab();
    put(lab, "beaker");
    lab.advance();
    put(lab, "scale");

    assertFalse(lab.hasNextStep());
    assertFalse(lab.advance());
    assertEquals(1, lab.getCurrentIndex());
  }

  @Test
  void goingBackIsAlwaysAllowedExceptFromTheFirstStep() {
    Lab lab = twoStepLab();
    assertFalse(lab.goBack());
    put(lab, "beaker");
    lab.advance();

    assertTrue(lab.goBack());
    assertEquals(0, lab.getCurrentIndex());
    assertFalse(lab.getCompletedSteps().contains(1));
  }

  @Test
  void stepsCanBeAttachedAgainAfterARestore() {
    Lab lab = twoStepLab();
    put(lab, "beaker");
    lab.advance();
    lab.attachSteps(List.of(needing(0, "beaker"), needing(1, "scale")));

    assertEquals("Hint 1", lab.message());
    assertEquals(1, lab.getCurrentIndex());
  }

  @Test
  void aMultiPartStepIsCompleteOnlyWhenEveryPartIs() {
    MultiPartStep both = new MultiPartStep(0, "Both", "Do both", "Hint", List.of(), false,
        List.of(needing(0, "beaker"), needing(0, "scale")));
    Lab lab = new Lab(List.of(both), new Bench());

    put(lab, "scale");
    assertEquals(StepStatus.INCOMPLETE, lab.status());
    put(lab, "beaker");
    assertEquals(StepStatus.COMPLETE, lab.status());
  }
}
