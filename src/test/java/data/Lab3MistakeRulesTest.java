package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Action;
import entity.ActionKind;
import entity.ApiType;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.StepStatus;
import entity.UnknownSample;
import entity.Vec2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Lab3MistakeRulesTest {

  private Lab lab;

  @BeforeEach
  void setUp() {
    lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
  }

  private String put(String templateId) {
    String id = lab.getBench().nextId(templateId);
    lab.getBench().addItem(new ItemFactory().create(ItemTemplates.find(templateId).orElseThrow(),
        id), new Vec2(5, 5));
    return id;
  }

  private StepStatus pourNaohIntoTheTestTube() {
    String bottle = put("naoh_bottle");
    String tube = put("test_tube");
    return lab.record(new Action(ActionKind.ADD, bottle, tube, 10, null, ""));
  }

  @Test
  void spawningAndMovingNeverRaisesAMistake() {
    String tube = put("conical_tube");

    assertEquals(StepStatus.COMPLETE, lab.record(Action.spawn(tube)));
    assertEquals(StepStatus.COMPLETE, lab.record(Action.move(tube, "unknown_vial-1")));
  }

  @Test
  void aNewLabFlagsAReagentInTheWrongTubeWithTheSpecificHint() {
    assertEquals(StepStatus.MISTAKE, pourNaohIntoTheTestTube());
    assertEquals("NaOH goes into the conical tube.", lab.message());
  }

  @Test
  void anActionTheStepDoesNotCallForIsFlaggedWithTheStepsHint() {
    String bottle = put("naoh_bottle");
    String tube = put("conical_tube");

    lab.record(new Action(ActionKind.ADD, bottle, tube, 10, null, ""));

    assertEquals(StepStatus.MISTAKE, lab.status());
    assertEquals(lab.currentStep().getHint(), lab.message());
  }

  @Test
  void undoingAMistakeClearsItAndTheRulesStillWorkAfterward() {
    InMemoryLabDataAccessObject dao = new InMemoryLabDataAccessObject();
    lab = Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
    dao.saveLab(lab);
    dao.pushSnapshot();
    pourNaohIntoTheTestTube();
    assertEquals(StepStatus.MISTAKE, lab.status());

    assertTrue(dao.popSnapshot());
    lab = dao.getLab();

    // A1's stub needs nothing on the bench, so once the mistake is gone the step is complete.
    assertEquals(StepStatus.COMPLETE, lab.status());
    assertEquals(StepStatus.MISTAKE, pourNaohIntoTheTestTube());
  }
}
