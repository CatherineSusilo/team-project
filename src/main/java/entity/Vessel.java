package entity;

/**
 * Anything on the bench that holds liquid or powder: the conical tube, test tube, graduated
 * cylinders, beaker, watch glass and the pipette. Its PNG will be picked from what's inside and
 * from its label, cover and cold flags.
 */
public class Vessel extends LabItem {
  private final Mixture mixture = new Mixture();  // what's inside, starts empty
  private final double capacity;                  // most mL it can hold
  private final double glassMassG;                // what the empty glass weighs on the scale
  private String label = "";
  private boolean covered;                        // A3: inverted beaker on the ether cylinder
  private boolean cold;                           // B2: has been in the ice bath
  private int stirCount;                          // B1: stirs with the stir rod
  private String holdingId;                       // A4: the tube this beaker holds, or null

  /**
   * Creates an empty, unlabelled vessel.
   *
   * @param id a unique id, e.g. "conical-1"
   * @param type which glassware it is
   * @param position where it sits on the bench
   * @param capacity the most it can hold, in mL
   * @param glassMassG what the empty vessel weighs, in grams
   */
  public Vessel(String id, ItemType type, Vec2 position, double capacity, double glassMassG) {
    super(id, type, position);
    this.capacity = capacity;
    this.glassMassG = glassMassG;
  }

  public Mixture getMixture() {
    return mixture;
  }

  public double getCapacity() {
    return capacity;
  }

  /**
   * What the scale shows for this vessel: the glass plus any solid in it. Liquids aren't
   * weighed in this lab.
   *
   * @return the mass in grams
   */
  public double massG() {
    double total = glassMassG;
    for (Layer layer : mixture.getLayers()) {
      for (double grams : layer.getSolids().values()) {
        total += grams;                           // e.g. 0.200 g of the unknown in A4
      }
    }
    return total;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isCovered() {
    return covered;
  }

  public void cover() {
    covered = true;
  }

  public boolean isCold() {
    return cold;
  }

  public void makeCold() {
    cold = true;
  }

  public int getStirCount() {
    return stirCount;
  }

  public void stir() {
    stirCount++;
  }

  public String getHoldingId() {
    return holdingId;
  }

  /**
   * Puts another vessel inside this one, like the conical tube standing in a beaker (A4).
   *
   * @param vesselId the vessel it now holds
   */
  public void hold(String vesselId) {
    holdingId = vesselId;
  }

  public void releaseHeld() {
    holdingId = null;
  }
}