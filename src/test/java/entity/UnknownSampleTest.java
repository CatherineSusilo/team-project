package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the student's unknown: guessing the hidden drug, and putting it in a tube.
 */
class UnknownSampleTest {
  private static final double MASS = 0.200;   // grams

  @Test
  void rightGuessIsTrue() {
    UnknownSample sample = new UnknownSample('A', ApiType.ASPIRIN, MASS);
    assertTrue(sample.checkGuess(ApiType.ASPIRIN));
  }

  @Test
  void wrongGuessIsFalse() {
    UnknownSample sample = new UnknownSample('A', ApiType.ASPIRIN, MASS);
    assertFalse(sample.checkGuess(ApiType.MEFENAMIC_ACID));
  }

  @Test
  void toMixtureHoldsAspirinAsPowder() {
    Mixture tube = new UnknownSample('A', ApiType.ASPIRIN, MASS).toMixture();
    Layer powder = tube.getLayers().get(0);
    assertEquals(1, tube.getLayers().size());                    // just one layer
    assertEquals(Phase.SOLID, powder.getPhase());                // no liquid yet
    assertEquals(MASS, powder.getSolids().get(Substances.ASPIRIN));
  }

  @Test
  void toMixtureHoldsMefenamicAcidAsPowder() {
    Mixture tube = new UnknownSample('B', ApiType.MEFENAMIC_ACID, MASS).toMixture();
    Layer powder = tube.getLayers().get(0);
    assertEquals(MASS, powder.getSolids().get(Substances.MEFENAMIC_ACID));
  }
}
