package data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ApiType;
import entity.UnknownSample;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests that students get a random unknown: either drug, batch A or B.
 */
class RandomAssignerTest {
  private static final int DRAWS = 100;   // enough draws to see every option

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
  void givesBothBatchesOverManyDraws() {
    RandomAssigner assigner = new RandomAssigner(new Random(42));
    boolean sawA = false;
    boolean sawB = false;
    for (int i = 0; i < DRAWS; i++) {
      char batch = assigner.assign().getBatch();
      if (batch == 'A') {
        sawA = true;
      } else if (batch == 'B') {
        sawB = true;
      }
    }
    assertTrue(sawA);
    assertTrue(sawB);
  }
}
