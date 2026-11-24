package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ApiType;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import java.util.List;
import org.junit.jupiter.api.Test;

class InMemoryLabDataAccessObjectLabTest {

  private final InMemoryLabDataAccessObject dao = new InMemoryLabDataAccessObject(List.of(),
      new FixedAssigner(new UnknownSample('B', ApiType.ASPIRIN)));

  @Test
  void theLabStartsWithTheAssignedUnknown() {
    assertEquals('B', dao.getLab().getUnknown().getBatch());
  }

  @Test
  void undoBringsBackTheEarlierBenchAndTheRestoredLabCanStillRun() {
    Lab lab = dao.getLab();
    String id = lab.getBench().nextId("conical_tube");
    lab.getBench().addItem(new ItemFactory().create(
        ItemTemplates.find("conical_tube").orElseThrow(), id), new Vec2(10, 10));
    dao.pushSnapshot();
    lab.getBench().removeItem(id);

    assertTrue(dao.popSnapshot());

    Lab restored = dao.getLab();
    assertNotNull(restored.getBench().getItem(id));
    assertTrue(restored.getBench().getItem(id) instanceof Vessel);
    assertEquals('B', restored.getUnknown().getBatch());
    assertTrue(restored.currentStep().getTitle().startsWith("A1"));
  }
}
