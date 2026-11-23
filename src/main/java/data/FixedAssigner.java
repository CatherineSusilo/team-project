package data;

import entity.UnknownSample;
import usecase.SampleAssignerInterface;

/**
 * Always gives the same unknown. For tests, the demo and debugging, never for students.
 */
public class FixedAssigner implements SampleAssignerInterface {
  private final UnknownSample sample;   // the one unknown it always gives

  /**
   * Creates the assigner.
   *
   * @param sample the unknown to give every time
   */
  public FixedAssigner(UnknownSample sample) {
    this.sample = sample;
  }

  @Override
  public UnknownSample assign() {
    return sample;
  }
}
