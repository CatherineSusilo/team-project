package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests the balance: A4 weighs the unknown into the conical tube, standing in a beaker.
 */
class ScaleTest {
  private static final double TOLERANCE = 1e-9;

  private final Bench bench = new Bench();
  private final Scale scale = new Scale("scale-1", new Vec2(0, 0));
  private final Vessel beaker = new Vessel("beaker-1", ItemType.BEAKER, new Vec2(0, 0), 50, 30);
  private final Vessel tube =
      new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 6);

  ScaleTest() {
    bench.addItem(scale, new Vec2(0, 0));
    bench.addItem(beaker, new Vec2(0, 0));
    bench.addItem(tube, new Vec2(0, 0));
  }

  /** The A4 setup: the tube stands in the beaker, and the beaker is on the scale. */
  private void beakerWithTubeOnScale() {
    beaker.hold("conical-1");
    scale.place("beaker-1");
  }

  @Test
  void emptyScaleReadsZero() {
    assertEquals("0.000", scale.display(bench));
  }

  @Test
  void readsTheBeakerAndTheTubeItHolds() {
    beakerWithTubeOnScale();
    assertEquals(36.0, scale.reading(bench), TOLERANCE);         // 30 g beaker + 6 g tube
    assertEquals("36.000", scale.display(bench));
  }

  @Test
  void tareMakesItZero() {
    beakerWithTubeOnScale();
    scale.tare(bench);
    assertEquals("0.000", scale.display(bench));
  }

  @Test
  void scoopShowsOnTheScale() {
    beakerWithTubeOnScale();
    scale.tare(bench);
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.200);   // A4
    assertEquals("0.200", scale.display(bench));
  }

  @Test
  void twoScoopsAddUp() {
    beakerWithTubeOnScale();
    scale.tare(bench);
    UnknownSample unknown = new UnknownSample('B', ApiType.MEFENAMIC_ACID);
    unknown.addTo(tube.getMixture(), 0.150);
    unknown.addTo(tube.getMixture(), 0.050);
    assertEquals("0.200", scale.display(bench));
  }

  @Test
  void removeEmptiesIt() {
    beakerWithTubeOnScale();
    scale.remove();
    assertNull(scale.getPlacedId());
    assertEquals("0.000", scale.display(bench));
  }

  @Test
  void neverShowsMinusZero() {
    beakerWithTubeOnScale();
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.1);
    scale.tare(bench);                                           // tare with rounding noise
    assertEquals("0.000", scale.display(bench));
  }
}
