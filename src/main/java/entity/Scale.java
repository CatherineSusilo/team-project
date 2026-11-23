package entity;

import java.util.Locale;

/**
 * The balance helps to display mass of what is placed on it.
 */
public class Scale extends LabItem {
  private String placedId;
  private double tare;                            // grams subtracted after pressing Tare

  /**
   * Creates an empty scale.
   *
   * @param id a unique id, e.g. "scale-1"
   * @param position where it sits on the bench
   */
  public Scale(String id, Vec2 position) {
    super(id, ItemType.SCALE, position);
  }

  public String getPlacedId() {
    return placedId;
  }

  /**
   * Puts a vessel on the scale.
   *
   * @param vesselId the vessel now on the scale
   */
  public void place(String vesselId) {
    placedId = vesselId;
  }

  public void remove() {
    placedId = null;
  }

  /**
   * Zeroes the scale with whatever is on it now, so only what's added afterwards counts.
   *
   * @param bench the bench, to find the vessel on the scale
   */
  public void tare(Bench bench) {
    tare = grossMass(bench);
  }

  /**
   * What the scale reads: what's on it, minus the tare.
   *
   * @param bench the bench, to find the vessel on the scale
   * @return the reading in grams
   */
  public double reading(Bench bench) {
    // subtracting the tare leaves tiny decimal noise (0.21000000000000085 instead of 0.210),
    // which would make a range check fail at its edges, so round to 3 decimals here
    return Math.round((grossMass(bench) - tare) * 1000) / 1000.0;
  }

  /**
   * The reading as the scale shows it, e.g. "0.200".
   *
   * @param bench the bench, to find the vessel on the scale
   * @return the reading with 3 decimals
   */
  public String display(Bench bench) {
    double grams = reading(bench);
    if (Math.abs(grams) < 0.0005) {
      grams = 0;
    }
    // Locale.ROOT so the scale always shows a dot ("0.200"). Without it, a computer set to
    // French (common in Canada) would show "0,200", and ScaleTest would fail on that machine.
    return String.format(Locale.ROOT, "%.3f", grams);
  }

  // Helper Function
  // Everything on the scale: the placed vessel, plus the tube it holds (A4: tube in a beaker).
  private double grossMass(Bench bench) {
    Vessel placed = findVessel(bench, placedId);
    if (placed == null) {
      return 0;
    }
    double total = placed.massG();
    Vessel held = findVessel(bench, placed.getHoldingId());
    if (held != null) {
      total += held.massG();
    }
    return total;
  }

  // Helper Function
  // The vessel with this id on the bench, or null.
  private static Vessel findVessel(Bench bench, String id) {
    if (id == null) {
      return null;
    }
    for (LabItem item : bench.getItems()) {
      if (item.getId().equals(id) && item instanceof Vessel) {
        return (Vessel) item;
      }
    }
    return null;
  }
}
