package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests the melting point app (B7): a pure, dry drug melts at its literature range; vanillin
 * or water left in the solid lowers it.
 */
class MeltingPointAppTest {
  private final MeltingPointApp app = new MeltingPointApp("melting_point_app-1", new Vec2(0, 0));
  private final Mixture sample = new Mixture();

  @Test
  void pureDryAspirinMeltsAtItsLiteratureRange() {
    sample.add(Substances.ASPIRIN, 0.15);
    assertRange(135, 136, app.measure(sample));
  }

  @Test
  void pureMefenamicAcidMeltsAtItsLiteratureRange() {
    sample.add(Substances.MEFENAMIC_ACID, 0.15);
    assertRange(230, 231, app.measure(sample));
  }

  @Test
  void vanillinLowersTheRange() {
    sample.add(Substances.ASPIRIN, 0.15);
    sample.add(Substances.VANILLIN, 0.04);               // B4's ether rinse wasn't done
    assertRange(125, 132, app.measure(sample));
  }

  @Test
  void wetSampleLowersTheRange() {
    sample.add(Substances.ASPIRIN, 0.15);
    sample.add(Substances.WATER, 0.5);                   // not dried under vacuum (B5)
    assertRange(125, 132, app.measure(sample));
  }

  @Test
  void wetAndImpureIsNoLowerThanEitherAlone() {
    sample.add(Substances.ASPIRIN, 0.15);
    sample.add(Substances.VANILLIN, 0.04);
    sample.add(Substances.WATER, 0.5);
    assertRange(125, 132, app.measure(sample));
  }

  @Test
  void noDrugMeasuresNothing() {
    assertNull(app.measure(sample));                     // nothing loaded
    sample.add(Substances.VANILLIN, 0.04);
    assertNull(app.measure(sample));                     // vanillin alone isn't the unknown
  }

  private static void assertRange(double lo, double hi, Range range) {
    assertEquals(lo, range.getLo(), 0.0001);
    assertEquals(hi, range.getHi(), 0.0001);
  }
}
