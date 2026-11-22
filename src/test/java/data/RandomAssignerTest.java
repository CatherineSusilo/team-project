package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ApiType;
import entity.UnknownSample;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests that students get a random unknown: either drug, batch A or B, always 0.200 g.
 */
class RandomAssignerTest {
  private static final int DRAWS = 100;   // enough draws to see both drugs

  @Test
  void givesBothDrugsOverManyDraws() {
    RandomAssigner assigner = new RandomAssigner(new Random(42));  // fixed seed: same every run
    boolean sawAspirin = false;
    boolean sawMefenamic = false;
    for (int i = 0; i < DRAWS; i++) {
      UnknownSample sample = assigner.assign();
      if (sample.checkGuess(ApiType.ASPIRIN)) {
        sawAspirin = true;
      } else {
        sawMefenamic = true;
      }
    }
    assertTrue(sawAspirin);
    assertTrue(sawMefenamic);
  }

  @Test
  void alwaysGivesTwoHundredMilligrams() {
    RandomAssigner assigner = new RandomAssigner(new Random(42));
    UnknownSample sample = assigner.assign();
    double grams = sample.toMixture().getLayers().get(0).getSolids().values().iterator().next();
    assertEquals(0.200, grams);
  }
}
