package entity;

/**
 * An item made from an {@link ItemTemplate}. It stands in for the vessel, bottle and equipment
 * classes until each one gets its own behaviour; {@link ItemFactory} is the only place that
 * has to change then.
 */
public class TemplateItem extends LabItem {

  private final ItemTemplate template;
  private String label = "";

  /**
   * Creates an item.
   *
   * @param id the item's id, "templateId-n"
   * @param template what it is made from
   * @param position where it sits on the bench
   */
  public TemplateItem(String id, ItemTemplate template, Vec2 position) {
    super(id, template.getType(), position);
    this.template = template;
  }

  public ItemTemplate getTemplate() {
    return template;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
