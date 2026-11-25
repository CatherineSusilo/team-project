package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Action;
import entity.ApiType;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.UnknownSample;
import entity.Vec2;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Undo on a real Lab 3: the restored lab has its earlier state and can still run. */
class LabSnapshotRestoreTest {

  private final InMemoryLabDataAccessObject dao = new InMemoryLabDataAccessObject(List.of(),
      new FixedAssigner(new UnknownSample('B', ApiType.ASPIRIN)));

  @Test
  void restoredLabHasItsEarlierBenchAndRecordAndStillRuns() {
    Lab lab = dao.getLab();
    lab.getRecord().setStartMassG(0.2);
    dao.pushSnapshot();
    lab.getRecord().setStartMassG(9);
    String id = lab.getBench().nextId("conical_tube");
    lab.getBench().addItem(new ItemFactory().create(
        ItemTemplates.find("conical_tube").orElseThrow(), id), new Vec2(10, 10));

    assertTrue(dao.popSnapshot());

    Lab restored = dao.getLab();
    assertEquals(0.2, restored.getRecord().getStartMassG());
    assertEquals(0, restored.getBench().countOf("conical_tube"));
    assertNotNull(restored.currentStep());
    assertEquals(restored.status(), restored.record(Action.spawn("conical_tube-1")));
  }
}
