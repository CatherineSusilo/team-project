package entity;

/**
 * The Büchner funnel with its filter paper (B2-B5). It sits on the filtration flask: the
 * liquid poured onto it ends up in the flask, and the solid stays on the paper, where it dries
 * while the vacuum is on.
 */
public class BuchnerFunnel extends Vessel {
  public static final double DRY_MINUTES = 10;

  private boolean hasPaper;
  private double minutesDried;

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

  /**
   * Whether the solid has dried long enough under vacuum.
   *
   * @return true after 10 minutes with the vacuum on
   */
  public boolean isDry() {
    return minutesDried >= DRY_MINUTES;
  }

  /**
   * Lets time pass for the funnel (the drying timer or its Skip button). It only dries while
   * it sits on an assembled flask and the vacuum is on. The liquid already went into the flask
   * when it was poured, so nothing drains here.
   *
   * @param m how many lab minutes passed
   * @param f the filtration flask
   * @param vacuumOn whether the vacuum tap is on
   */
  public void advance(double m, FiltrationFlask f, boolean vacuumOn) {
    if (vacuumOn && f.isAssembled() && getId().equals(f.getFunnelId())) {
      minutesDried += m;
    }
  }
}