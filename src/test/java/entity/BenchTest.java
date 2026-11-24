package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BenchTest {

  private static class TestItem extends LabItem {

    TestItem(String id) {
      super(id, ItemType.CONICAL_TUBE, new Vec2(0, 0));
    }
  }

  @Test
  void addedItemSitsWhereItWasPlaced() {
    Bench bench = new Bench();
    bench.addItem(new TestItem("tube-1"), new Vec2(10, 20));

    assertEquals(1, bench.getItems().size());
    assertEquals(new Vec2(10, 20), bench.getItems().get(0).getPosition());
  }

  @Test
  void removedItemIsGone() {
    Bench bench = new Bench();
    bench.addItem(new TestItem("tube-1"), new Vec2(10, 20));
    bench.removeItem("tube-1");

    assertTrue(bench.getItems().isEmpty());
  }

  @Test
  void itemListCantBeChangedFromOutside() {
    Bench bench = new Bench();
    bench.addItem(new TestItem("tube-1"), new Vec2(0, 0));

    try {
      bench.getItems().clear();
    } catch (UnsupportedOperationException expected) {
      // the list is read-only
    }
    assertEquals(1, bench.getItems().size());
  }

  @Test
  void nearbyItemsAreNearAndFarOnesAreNot() {
    TestItem first = new TestItem("a");
    TestItem second = new TestItem("b");
    TestItem far = new TestItem("c");
    first.setPosition(new Vec2(0, 0));
    second.setPosition(new Vec2(30, 40));
    far.setPosition(new Vec2(500, 500));

    assertTrue(first.isNear(second));
    assertFalse(first.isNear(far));
  }

  @Test
  void itemIsFoundByIdAndMissingOneIsNull() {
    Bench bench = new Bench();
    TestItem tube = new TestItem("conical_tube-1");
    bench.addItem(tube, new Vec2(1, 2));

    assertSame(tube, bench.getItem("conical_tube-1"));
    assertNull(bench.getItem("nothing-1"));
  }

  @Test
  void idsCountUpPerTemplateAndAreNeverReused() {
    Bench bench = new Bench();
    assertEquals("pipette-1", bench.nextId("pipette"));
    assertEquals("pipette-2", bench.nextId("pipette"));
    assertEquals("beaker-1", bench.nextId("beaker"));
    bench.addItem(new TestItem("pipette-2"), new Vec2(0, 0));
    bench.removeItem("pipette-2");

    assertEquals("pipette-3", bench.nextId("pipette"));
  }

  @Test
  void itemsAreCountedByTemplate() {
    Bench bench = new Bench();
    bench.addItem(new TestItem("pipette-1"), new Vec2(0, 0));
    bench.addItem(new TestItem("pipette-2"), new Vec2(0, 0));
    bench.addItem(new TestItem("beaker-1"), new Vec2(0, 0));

    assertEquals(2, bench.countOf("pipette"));
    assertEquals(1, bench.countOf("beaker"));
    assertEquals(0, bench.countOf("scale"));
    assertEquals("conical_tube", Bench.templateIdOf("conical_tube-12"));
  }
}
