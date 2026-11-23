package data;

import entity.ApiType;
import entity.UnknownSample;
import java.util.Random;
import usecase.SampleAssignerInterface;

/**
 * Gives each student a random unknown: batch A or B, with aspirin or mefenamic acid
 * hidden inside.
 */
public class RandomAssigner implements SampleAssignerInterface {
  private final Random random;

  /**
   * Creates the assigner.
   *
   * @param random where the random numbers come from, so we can test with a fixed seed
   */
  public RandomAssigner(Random random) {
    this.random = random;
  }

  @Override
  public UnknownSample assign() {
    ApiType[] apis = ApiType.values();
    ApiType api = apis[random.nextInt(apis.length)];
    char batch = (char) ('A' + random.nextInt(2));
    return new UnknownSample(batch, api);
  }
}