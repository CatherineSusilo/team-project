package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BenchTest {

  @Test
  void addedItemCanBeFoundAndRemoved() {
    Bench bench = new Bench();
    bench.add(new LabItem("tube-1", ItemType.CONICAL_TUBE, new Vec2(10, 20)));

    assertEquals(new Vec2(10, 20), bench.find("tube-1").orElseThrow().getPosition());

    bench.remove("tube-1");
    assertTrue(bench.find("tube-1").isEmpty());
  }

  @Test
  void itemListCantBeChangedFromOutside() {
    Bench bench = new Bench();
    bench.add(new LabItem("tube-1", ItemType.CONICAL_TUBE, new Vec2(0, 0)));

    try {
      bench.getItems().clear();
    } catch (UnsupportedOperationException expected) {
      // fine
    }
    assertEquals(1, bench.getItems().size());
  }
}
