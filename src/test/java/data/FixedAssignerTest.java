package data;

import static org.junit.jupiter.api.Assertions.assertSame;

import entity.ApiType;
import entity.UnknownSample;
import org.junit.jupiter.api.Test;

/**
 * Tests that the fixed assigner always gives the same unknown.
 */
class FixedAssignerTest {

  @Test
  void alwaysGivesTheSameSample() {
    UnknownSample sample = new UnknownSample('A', ApiType.ASPIRIN);
    FixedAssigner assigner = new FixedAssigner(sample);
    assertSame(sample, assigner.assign());
    assertSame(sample, assigner.assign());
  }
}
