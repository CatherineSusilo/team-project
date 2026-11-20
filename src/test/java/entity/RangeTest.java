package entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RangeTest {
  private final Range meltingPoint = new Range(135, 136);

  @Test
  void containsValueInside() {
    assertTrue(meltingPoint.contains(135.5));
  }

  @Test
  void doesNotContainBelow() {
    assertFalse(meltingPoint.contains(134.9));
  }
}