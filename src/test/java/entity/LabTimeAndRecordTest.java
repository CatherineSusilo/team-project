package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LabTimeAndRecordTest {

  private static Lab lab() {
    CheckStep step = new CheckStep(0, "Step 0", "Do step 0", "Hint 0", List.of(), false,
        l -> false, action -> true);
    return new Lab(List.of(step), new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  }

  @Test
  void advancingTimeSettlesAMixedVessel() {
    Lab lab = lab();
    String id = lab.getBench().nextId("conical_tube");
    Vessel tube = (Vessel) new ItemFactory().create(
        ItemTemplates.find("conical_tube").orElseThrow(), id);
    lab.getBench().addItem(tube, new Vec2(5, 5));
    tube.getMixture().mix(1);
    assertFalse(tube.getMixture().isSettled());

    lab.advanceTime(1);

    assertTrue(tube.getMixture().isSettled());
  }

  @Test
  void timeCannotGoBackwards() {
    assertThrows(IllegalArgumentException.class, () -> lab().advanceTime(-1));
  }

  @Test
  void startsWithAnEmptyRecord() {
    LabRecord record = lab().getRecord();

    assertEquals(0, record.getStartMassG());
    assertFalse(record.isUnknownRecorded());
    assertNull(record.getMixingPipetteId());
  }
}
