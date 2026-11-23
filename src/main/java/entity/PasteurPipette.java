package entity;

/**
 * A Pasteur pipette. It is considered a vessel.
 * It remembers whether it is still clean. The lab manual says not to reuse a dirty
 * pipette: one pipette per solvent so the stocks aren't contaminated (A3), a new pipette for
 * mixing (A5), and a new, dry one for the ether rinse (B4). A used pipette can carry leftover
 * liquid into the wrong place, so the app shows it as used and flags it if the student
 * reuses it where a new one is needed.
 */
public class PasteurPipette extends Vessel {
  // The most a pipette holds, in mL
  public static final double CAPACITY_ML = 2.0;

  private boolean clean = true;

  /**
   * Creates a new, clean, empty pipette.
   *
   * @param id a unique id, e.g. "pipette-1"
   * @param position where it sits on the bench
   */
  public PasteurPipette(String id, Vec2 position) {
    super(id, ItemType.PIPETTE, position, CAPACITY_ML, 0);
  }

  public boolean isClean() {
    return clean;
  }

  public void markUsed() {
    clean = false;
  }
}
