package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Action;
import entity.ActionKind;
import entity.ApiType;
import entity.Bench;
import entity.ItemType;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabStep;
import entity.Mixture;
import entity.StepStatus;
import entity.TemplateItem;
import entity.Substances;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
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

  // ----- A8: settle, then move the water layer -----

  private static final int A8 = 7;

  /** A8's bench: the conical tube just mixed in A7 (cloudy) and the empty test tube. */
  private static Lab atA8(Vessel conical, Vessel testTube) {
    Lab lab = new Lab(List.of(Lab3Definition.steps().get(A8)), new Bench(),
        new UnknownSample('A', ApiType.ASPIRIN));
    lab.getBench().addItem(conical, new Vec2(0, 0));
    lab.getBench().addItem(testTube, new Vec2(0, 0));
    conical.getMixture().add(Substances.WATER, 2);
    conical.getMixture().add(Substances.ETHER, 4);
    conical.getMixture().mix(Mixture.FULL_MIX);
    return lab;
  }

  @Test
  void a8WaitsForTheLayersThenNeedsTheWaterMoved() {
    Vessel conical = new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    Vessel testTube = new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);
    Lab lab = atA8(conical, testTube);
    assertEquals(1, lab.currentStep().waitMinutes(lab));   // the timer bar shows

    lab.advanceTime(1);                                     // the Wait use case
    assertEquals(0, lab.currentStep().waitMinutes(lab));
    assertEquals(StepStatus.INCOMPLETE, lab.status());     // settled, water not moved yet

    testTube.getMixture().pour(conical.getMixture().drawFromBottom(1.8));
    assertEquals(StepStatus.COMPLETE, lab.status());
  }

  @Test
  void a8TakingEtherIntoTheTestTubeIsNotDone() {
    Vessel conical = new Vessel("conical", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);
    Vessel testTube = new Vessel("testTube", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);
    Lab lab = atA8(conical, testTube);
    lab.advanceTime(1);
    testTube.getMixture().pour(conical.getMixture().drawFromBottom(2));     // all the water
    testTube.getMixture().pour(conical.getMixture().drawFromBottom(0.5));   // then ether
    assertEquals(StepStatus.INCOMPLETE, lab.status());
  }

  @Test
  void a8ExpectsPouring() {
    LabStep a8 = Lab3Definition.steps().get(A8);
    assertTrue(a8.expects(new Action(ActionKind.MOVE_LIQUID, "conical", "pipette", 2, null,
        "")));
    assertTrue(a8.getTitle().startsWith("A8"));
  }
}