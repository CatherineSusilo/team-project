package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The items currently on the student's bench.
 */
public class Bench implements BenchInterface {

  private final List<LabItem> items = new ArrayList<>();
  private final Map<String, Integer> idCounts = new HashMap<>();

  @Override
  public void addItem(LabItem item, Vec2 position) {
    item.setPosition(position);
    items.add(item);
  }

  @Override
  public void removeItem(String id) {
    items.removeIf(item -> item.getId().equals(id));
  }

  @Override
  public List<LabItem> getItems() {
    return List.copyOf(items);
  }

  /**
   * Looks an item up.
   *
   * @param id the item's id
   * @return the item, or null if it isn't on the bench
   */
  public LabItem getItem(String id) {
    for (LabItem item : items) {
      if (item.getId().equals(id)) {
        return item;
      }
    }
    return null;
  }

  /**
   * Makes the id for a new item, "templateId-n", counting up so that an id is never reused
   * even after its item has been removed.
   *
   * @param templateId the template the new item is made from
   * @return a new id such as "conical_tube-1"
   */
  public String nextId(String templateId) {
    int count = idCounts.merge(templateId, 1, Integer::sum);
    return templateId + "-" + count;
  }

  /**
   * Counts the items on the bench that were made from a template.
   *
   * @param templateId the template's id
   * @return how many of its items are on the bench
   */
  public int countOf(String templateId) {
    int count = 0;
    for (LabItem item : items) {
      if (templateIdOf(item.getId()).equals(templateId)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Reads the template id out of an item id made by {@link #nextId(String)}.
   *
   * @param itemId an item id such as "conical_tube-1"
   * @return the template id, such as "conical_tube"
   */
  public static String templateIdOf(String itemId) {
    int dash = itemId.lastIndexOf('-');
    return dash < 0 ? itemId : itemId.substring(0, dash);
  }
}
