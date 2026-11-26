package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the clamp stand: clamping the filtration flask to it (B2) and letting it go.
 */
class ClampStandTest {
  private final ClampStand stand = new ClampStand("clamp_stand-1", new Vec2(0, 0));
  private final FiltrationFlask flask = new FiltrationFlask("flask-1", new Vec2(0, 0), 250, 150);

  @Test
  void newStandHoldsNothing() {
    assertNull(stand.getFlaskId());
  }

  @Test
  void clampHoldsTheFlask() {
    stand.clamp(flask);
    assertEquals("flask-1", stand.getFlaskId());
    assertTrue(flask.isClamped());                        // the flask knows too
  }

  @Test
  void releaseLetsTheFlaskGo() {
    stand.clamp(flask);
    stand.release(flask);
    assertNull(stand.getFlaskId());
    assertFalse(flask.isClamped());
  }
}
