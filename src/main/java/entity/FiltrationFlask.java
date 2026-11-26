package entity;

/**
 * The flask under the Büchner funnel. It's clamped to the clamp stand, the funnel
 * sits on top, and the liquid poured onto the funnel ends up in here (the filtrate).
 */
public class FiltrationFlask extends Vessel {   // ItemType.FILTRATION_FLASK (250 mL)
  private String funnelId;                      // UML: the funnel sitting on it, null if none
  private boolean clamped;                      // added: so isAssembled knows

  /**
   * Creates an empty flask, not clamped and with no funnel on it.
   *
   * @param id a unique id, e.g. "flask-1"
   * @param position where it sits on the bench
   * @param capacity the most it holds, in mL (250)
   * @param glassMassG what the empty flask weighs, in grams
   */
  public FiltrationFlask(String id, Vec2 position, double capacity, double glassMassG) {
    super(id, ItemType.FILTRATION_FLASK, position, capacity, glassMassG);
  }

  public String getFunnelId() {
    return funnelId;
  }

  public boolean isClamped() {
    return clamped;
  }

  /**
   * Whether the filtration is set up: clamped to the stand with a funnel on top (B2).
   *
   * @return true once both are done
   */
  public boolean isAssembled() {
    return isClamped() && getFunnelId() != null;
  }

  void setClamped(boolean clamped) {
    this.clamped = clamped;
  }

  public void placeFunnel(String funnelId){
    this.funnelId = funnelId;
  }
  
  public void removeFunnel(){
    funnelId = null;
  }

}