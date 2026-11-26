package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the Büchner funnel: filter paper (ADD_PAPER) and drying under vacuum (B5).
 */
class BuchnerFunnelTest {
  private final ClampStand stand = new ClampStand("clamp_stand-1", new Vec2(0, 0));
  private final FiltrationFlask flask = new FiltrationFlask("flask-1", new Vec2(0, 0), 250, 150);
  private final BuchnerFunnel funnel = new BuchnerFunnel("funnel-1", new Vec2(0, 0), 10, 60);

  /** The filtration set up, with the solid already collected on the paper. */
  private void assembledWithSolid() {
    stand.clamp(flask);
    flask.placeFunnel("funnel-1");
    funnel.addPaper();
    funnel.getMixture().add(Substances.ASPIRIN, 0.15);
  }

  @Test
  void newFunnelHasNoPaper() {
    assertFalse(funnel.hasPaper());
    assertFalse(funnel.isDry());
  }

  @Test
  void addPaperPutsThePaperIn() {
    funnel.addPaper();
    assertTrue(funnel.hasPaper());
  }

  @Test
  void driesAfterTenMinutesWithTheVacuumOn() {
    assembledWithSolid();
    funnel.advance(BuchnerFunnel.DRY_MINUTES, flask, true);
    assertTrue(funnel.isDry());
  }

  @Test
  void doesNotDryWithTheVacuumOff() {
    assembledWithSolid();
    funnel.advance(BuchnerFunnel.DRY_MINUTES, flask, false);
    assertFalse(funnel.isDry());
  }

  @Test
  void doesNotDryInLessThanTenMinutes() {
    assembledWithSolid();
    funnel.advance(1, flask, true);                       // e.g. a 1-minute settle timer
    assertFalse(funnel.isDry());
  }

  @Test
  void doesNotDryOffTheFlask() {
    funnel.addPaper();
    funnel.getMixture().add(Substances.ASPIRIN, 0.15);
    funnel.advance(BuchnerFunnel.DRY_MINUTES, flask, true);   // flask not set up
    assertFalse(funnel.isDry());
  }

  @Test
  void anEmptyFunnelHasNothingToDry() {
    stand.clamp(flask);
    flask.placeFunnel("funnel-1");
    funnel.advance(BuchnerFunnel.DRY_MINUTES, flask, true);   // B2's ice-bath timer
    assertFalse(funnel.isDry());
  }

  @Test
  void rinsesAreCounted() {
    funnel.addRinse();
    funnel.addRinse();
    assertEquals(2, funnel.getRinses());
  }
}
