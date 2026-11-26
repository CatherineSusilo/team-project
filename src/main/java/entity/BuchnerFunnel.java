package entity;

/**
 * The Büchner funnel with its filter paper. It sits on the filtration flask: the
 * liquid poured onto it ends up in the flask, and the solid stays on the paper, where it dries
 * while the vacuum is on.
 */
public class BuchnerFunnel extends Vessel {
  // How long the solid dries under vacuum (lab manual B5: ~10 minutes).
  public static final double DRY_MINUTES = 10;

  private boolean hasPaper;
  private int rinses;
  private boolean dry;

  /**
   * Creates an empty funnel with no filter paper in it.
   *
   * @param id a unique id, e.g. "funnel-1"
   * @param position where it sits on the bench
   * @param capacity the most it holds, in mL
   * @param glassMassG what the empty funnel weighs, in grams
   */
  public BuchnerFunnel(String id, Vec2 position, double capacity, double glassMassG) {
    super(id, ItemType.BUCHNER_FUNNEL, position, capacity, glassMassG);
  }

  public boolean hasPaper() {
    return hasPaper;
  }

  public void addPaper() {
    hasPaper = true;
  }

  public int getRinses() {
    return rinses;
  }

  public void addRinse() {
    rinses++;
  }

  /**
   * Whether the solid has dried long enough under vacuum.
   *
   * @return true after {@value #DRY_MINUTES} lab minutes with the vacuum on
   */
  public boolean isDry() {
    return dry;
  }

  /**
   * Lets time pass for the funnel (the drying timer or its Skip button). It dries once
   * {@value #DRY_MINUTES} minutes pass while it has solid on it, sits on an assembled flask and
   * the vacuum is on.
   * The liquid already went into the flask when it was poured, so nothing drains here.
   *
   * @param m how many lab minutes passed
   * @param f the filtration flask
   * @param vacuumOn whether the vacuum tap is on
   */
  public void advance(double m, FiltrationFlask f, boolean vacuumOn) {
    // an empty funnel has nothing to dry (e.g. B2's 10-minute ice-bath timer with the vacuum
    // already on must not count as drying the solid that only gets poured on in B3)
    boolean hasSolid = !getMixture().getLayers().isEmpty();
    if (m >= DRY_MINUTES && vacuumOn && hasSolid && f.isAssembled()
        && getId().equals(f.getFunnelId())) {
      dry = true;
    }
  }
}
