package entity;

/**
 * Makes the items the student takes from the tray.
 */
public class ItemFactory {

  /**
   * Makes an item from a template.
   *
   * @param template what to make
   * @param id the new item's id
   * @return the item, at the bench's origin until it is placed
   */
  public LabItem create(ItemTemplate template, String id) {
    return new TemplateItem(id, template, new Vec2(0, 0));
  }
}
