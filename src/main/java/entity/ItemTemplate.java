package entity;

/**
 * What the tray offers, such as "naoh_bottle" or "stir_rod": the settings an item is made
 * from.
 */
public final class ItemTemplate {

  private final String templateId;
  private final ItemType type;
  private final Substance substance;
  private final double capacity;
  private final double glassMassG;

  /**
   * Creates a template.
   *
   * @param templateId the id the tray and the lab definition use
   * @param type what kind of item it makes
   * @param substance what a stock bottle holds, or null for anything else
   * @param capacity what a vessel holds in mL, else 0
   * @param glassMassG what an empty vessel weighs in grams, else 0
   */
  public ItemTemplate(String templateId, ItemType type, Substance substance,
      double capacity, double glassMassG) {
    this.templateId = templateId;
    this.type = type;
    this.substance = substance;
    this.capacity = capacity;
    this.glassMassG = glassMassG;
  }

  public String getTemplateId() {
    return templateId;
  }

  public ItemType getType() {
    return type;
  }

  public Substance getSubstance() {
    return substance;
  }


  public double getCapacity() {
    return capacity;
  }

  public double getGlassMassG() {
    return glassMassG;
  }
}
