package entity;

/**
 * The clamp stand in the fume hood. The filtration flask is clamped to it, so the
 * Büchner funnel can sit on top.
 */
public class ClampStand extends LabItem {
  private String flaskId;

  /**
   * Creates a clamp stand with nothing clamped to it.
   *
   * @param id a unique id, e.g. "clamp_stand-1"
   * @param position where it sits on the bench
   */
  public ClampStand(String id, Vec2 position) {
    super(id, ItemType.CLAMP_STAND, position);
  }

  public String getFlaskId() {
    return flaskId;
  }

  /**
   * Clamps a flask to the stand
   *
   * @param flask the flask to clamp
   */
  public void clamp(FiltrationFlask flask) {
    flaskId = flask.getId();
    flask.setClamped(true);
  }

  /**
   * Lets the flask go again (it was moved away from the stand).
   *
   * @param flask the flask to release
   */
  public void release(FiltrationFlask flask) {
    flaskId = null;
    flask.setClamped(false);
  }
}
