package entity;

/**
 * A position on the bench, in screen coordinates.
 */
public final class Vec2 {

  private final double horizontal;
  private final double vertical;

  /**
   * Creates a position.
   *
   * @param x the horizontal coordinate
   * @param y the vertical coordinate
   */
  public Vec2(double x, double y) {
    this.horizontal = x;
    this.vertical = y;
  }

  public double getX() {
    return horizontal;
  }

  public double getY() {
    return vertical;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Vec2)) {
      return false;
    }
    Vec2 that = (Vec2) other;
    return horizontal == that.horizontal && vertical == that.vertical;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(horizontal) * 31 + Double.hashCode(vertical);
  }
}