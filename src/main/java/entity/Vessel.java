package entity;

import java.util.Map;

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
   * What the scale shows for this vessel: the glass plus the drug and vanillin in it, whether
   * they're powder or dissolved. Liquids and reagents (NaOH, HCl) are in mL, so they aren't
   * weighed.
   *
   * @return the mass in grams
   */
  public double massG() {
    double total = glassMassG;
    for (Layer layer : mixture.getLayers()) {
      total += sampleGrams(layer.getSolids()) + sampleGrams(layer.getDissolved());
    }
    return total;
  }

  // Helper Function
  // Grams of the sample (drug + vanillin) in these amounts; NaOH and HCl are in mL, so skip them.
  private double sampleGrams(Map<Substance, Double> amounts) {
    double grams = 0;
    for (Map.Entry<Substance, Double> entry : amounts.entrySet()) {
      if (entry.getKey() != Substances.NAOH && entry.getKey() != Substances.HCL) {
        grams += entry.getValue();
      }
    }
    return grams;
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