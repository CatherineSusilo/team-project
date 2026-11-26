package entity;

/**
 * The vacuum tap in the fume hood. Turning it on pulls the liquid through the Büchner funnel
 * and dries the solid (B5); its PNG shows it on or off.
 */
public class VacuumTap extends LabItem {
  private boolean on;

  /**
   * Creates a vacuum tap that is off.
   *
   * @param id a unique id, e.g. "vacuum_tap-1"
   * @param position where it sits on the bench
   */
  public VacuumTap(String id, Vec2 position) {
    super(id, ItemType.VACUUM_TAP, position);
  }

  public boolean isOn() {
    return on;
  }

  public void setOn(boolean on) {
    this.on = on;
  }
}