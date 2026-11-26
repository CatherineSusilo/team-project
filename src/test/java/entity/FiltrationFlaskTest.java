package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the filtration flask: it's only set up (assembled) once it's clamped with a funnel on.
 */
class FiltrationFlaskTest {
  private final ClampStand stand = new ClampStand("clamp_stand-1", new Vec2(0, 0));
  private final FiltrationFlask flask = new FiltrationFlask("flask-1", new Vec2(0, 0), 250, 150);

  @Test
  void newFlaskIsNotAssembled() {
    assertFalse(flask.isClamped());
    assertNull(flask.getFunnelId());
    assertFalse(flask.isAssembled());
  }

  @Test
  void clampedWithoutAFunnelIsNotAssembled() {
    stand.clamp(flask);
    assertFalse(flask.isAssembled());
  }

  @Test
  void funnelWithoutTheClampIsNotAssembled() {
    flask.placeFunnel("funnel-1");
    assertFalse(flask.isAssembled());
  }

  @Test
  void clampedWithAFunnelIsAssembled() {
    stand.clamp(flask);
    flask.placeFunnel("funnel-1");
    assertEquals("funnel-1", flask.getFunnelId());
    assertTrue(flask.isAssembled());                      // filtration--assembled PNG
  }

  @Test
  void removingTheFunnelUndoesIt() {
    stand.clamp(flask);
    flask.placeFunnel("funnel-1");
    flask.removeFunnel();
    assertFalse(flask.isAssembled());
  }

  @Test
  void itIsAVesselSoTheFiltrateCanGoIn() {
    flask.getMixture().add(Substances.WATER, 3);
    assertEquals(3.0, flask.getMixture().totalVolume(), 1e-9);
  }
}
