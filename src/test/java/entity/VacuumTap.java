package entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the vacuum tap: off until turned on (VACUUM_ON / VACUUM_OFF).
 */
class VacuumTapTest {
  private final VacuumTap tap = new VacuumTap("vacuum_tap-1", new Vec2(0, 0));

  @Test
  void newTapIsOff() {
    assertFalse(tap.isOn());
  }

  @Test
  void turnsOnAndOff() {
    tap.setOn(true);
    assertTrue(tap.isOn());                               // vacuum-tap--on PNG
    tap.setOn(false);
    assertFalse(tap.isOn());
  }
}
