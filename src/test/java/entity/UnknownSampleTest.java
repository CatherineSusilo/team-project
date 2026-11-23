package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the student's unknown: its batch letter, guessing the hidden drug, and scooping it
 * into a tube.
 */
class UnknownSampleTest {

  @Test
  void keepsBatchLetter() {
    UnknownSample sample = new UnknownSample('B', ApiType.ASPIRIN);
    assertEquals('B', sample.getBatch());
  }

  @Test
  void rightGuessIsTrue() {
    UnknownSample sample = new UnknownSample('A', ApiType.ASPIRIN);
    assertTrue(sample.checkGuess(ApiType.ASPIRIN));
  }

  @Test
  void wrongGuessIsFalse() {
    UnknownSample sample = new UnknownSample('A', ApiType.ASPIRIN);
    assertFalse(sample.checkGuess(ApiType.MEFENAMIC_ACID));
  }

  @Test
  void scoopAddsTheDrugAsPowder() {
    Mixture tube = new Mixture();
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube, 0.200);
    Layer powder = tube.getLayers().get(0);
    assertEquals(Phase.SOLID, powder.getPhase());                // no liquid yet
    assertEquals(0.200, powder.getSolids().get(Substances.ASPIRIN));
  }

  @Test
  void repeatScoopsAddUp() {
    Mixture tube = new Mixture();
    UnknownSample sample = new UnknownSample('B', ApiType.MEFENAMIC_ACID);
    sample.addTo(tube, 0.150);
    sample.addTo(tube, 0.050);
    double grams = tube.getLayers().get(0).getSolids().get(Substances.MEFENAMIC_ACID);
    assertEquals(0.200, grams, 1e-9);
  }
}
