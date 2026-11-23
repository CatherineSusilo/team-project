package entity;

/**
 * A bottle the student only takes from: the NaOH and HCl dropper bottles and the water and
 * ether stock bottles. It never runs out and never changes.
 */
public class StockBottle extends LabItem {
  // One drop from a dropper, in mL (10 drops = 0.5 mL).
  public static final double DROP_ML = 0.05;

  private final Substance substance;   // what it pours, e.g. Substances.NAOH

  /**
   * Creates a stock bottle.
   *
   * @param id a unique id, e.g. "naoh-bottle"
   * @param type DROPPER for NaOH/HCl, SOLVENT_BOTTLE for water/ether
   * @param position where it sits on the bench
   * @param substance what it pours
   */
  public StockBottle(String id, ItemType type, Vec2 position, Substance substance) {
    super(id, type, position);
    this.substance = substance;
  }

  public Substance getSubstance() {
    return substance;
  }
}
