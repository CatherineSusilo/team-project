package entity;

/**
 * Works out the B6 percent recovery from the start and final masses.
 */
public class RecoveryCalculator {

  /**
   * Returns how much of the start mass was recovered, as a percent rounded to 0.1.
   * Over 100 is returned as is (a wet solid weighs too much).
   *
   * @param startG the unknown weighed into the tube in A4, in grams
   * @param endG the dry solid weighed in B6 (watch glass with solid minus empty glass), in grams
   * @return endG / startG x 100, rounded to one decimal
   * @throws IllegalArgumentException if startG is not more than 0
   */
  public double percentRecovery(double startG, double endG) {
    if (!(startG > 0)) {                                          // also catches NaN
      throw new IllegalArgumentException("Start mass must be more than 0 g, was " + startG);
    }
    return Math.round(endG / startG * 1000) / 10.0;               // x100 for %, x10 for 0.1
  }
}
