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
  private String mixingTubeId;        // the tube this pipette is mixing (A5, A7), or null

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

  /**
   * Records a pump in this tube. Only a new pipette becomes that tube's mixing pipette, so an
   * ether or water pipette never does.
   *
   * @param tubeId the tube being mixed
   */
  public void markMixing(String tubeId) {
    if (clean) {
      mixingTubeId = tubeId;
    }
    clean = false;
  }

  /**
   * Whether this pipette may mix this tube: it's new, or it's already the one mixing it (A5, A7).
   *
   * @param tubeId the tube to mix
   * @return true if it's new or already mixing this tube
   */
  public boolean canMix(String tubeId) {
    return clean || tubeId.equals(mixingTubeId);
  }
}
