package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Action;
import entity.ActionKind;
import entity.ApiType;
import entity.Bench;
import entity.BuchnerFunnel;
import entity.ClampStand;
import entity.FiltrationFlask;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabItem;
import entity.LabStep;
import entity.Operation;
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

  // ----- B2: the filtration set up while the test tube cools -----

  private static final int B2 = 10;
  private final LabStep b2 = Lab3Definition.steps().get(B2);

  /** Takes an item from the tray, like dragging it onto the bench. */
  private static LabItem take(Lab lab, String templateId) {
    Bench bench = lab.getBench();
    LabItem item = new ItemFactory().create(ItemTemplates.find(templateId).orElseThrow(),
        bench.nextId(templateId));
    bench.addItem(item, new Vec2(0, 0));
    return item;
  }

  /** B2's items on the bench: the ice bath, clamp stand, flask and funnel, not set up yet. */
  private static Lab b2ItemsOnTheBench() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    take(lab, "ice_bath");
    take(lab, "clamp_stand");
    take(lab, "flask");
    take(lab, "funnel");
    return lab;
  }

  /** Clamps the flask and puts the funnel on it (what MoveItem's drops will do). */
  private static BuchnerFunnel assemble(Lab lab) {
    ClampStand stand = (ClampStand) lab.getBench().getItem("clamp_stand-1");
    FiltrationFlask flask = (FiltrationFlask) lab.getBench().getItem("flask-1");
    stand.clamp(flask);
    flask.placeFunnel("funnel-1");
    return (BuchnerFunnel) lab.getBench().getItem("funnel-1");
  }

  @Test
  void b2IsNotDoneWithTheItemsJustOnTheBench() {
    assertFalse(b2.isComplete(b2ItemsOnTheBench()));
  }

  @Test
  void b2IsNotDoneWithoutFilterPaper() {
    Lab lab = b2ItemsOnTheBench();
    assemble(lab);
    assertFalse(b2.isComplete(lab));
  }

  @Test
  void b2IsDoneOnceTheFiltrationIsSetUpWithPaper() {
    Lab lab = b2ItemsOnTheBench();
    assemble(lab).addPaper();
    assertTrue(b2.isComplete(lab));
  }

  @Test
  void b2IsNotDoneWithoutTheIceBath() {
    Lab lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    take(lab, "clamp_stand");
    take(lab, "flask");
    take(lab, "funnel");
    assemble(lab).addPaper();
    assertFalse(b2.isComplete(lab));
  }

  @Test
  void addingThePaperBelongsToB2() {
    assertTrue(b2.expects(new Action(ActionKind.USE, "funnel-1", null, 0, Operation.ADD_PAPER,
        "")));
    assertFalse(b2.expects(new Action(ActionKind.MIX, "pipette-1", "test_tube-1", 5, null,
        "")));
  }
}
