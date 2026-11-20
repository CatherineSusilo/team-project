package entity;

/**
 * A low-to-high band of numbers, inclusive, e.g. a melting point of 135–136 °C.
 */
public final class Range {
  private final double lo;
  private final double hi;

  /**
   * Creates a range of numbers using a low value and high value, inclusive.
   * e.g. a melting point of 135–136 °C.
   *
   * @param lo the lower bound
   * @param hi the upper bound
   */
  public Range(double lo, double hi) {
    if (lo > hi) {
      throw new IllegalArgumentException("lo must not be greater than hi");
    }
    this.lo = lo;
    this.hi = hi;
  }

  public double getLo() {
    return lo;
  }

  public double getHi() {
    return hi;
  }

  /**
   * Checks whether a value is inside the range, inclusive.
   *
   * @param value the number to check
   * @return true if lo ≤ value ≤ hi
   */
  public boolean contains(double value) {
    return value >= lo && value <= hi;
  }
}