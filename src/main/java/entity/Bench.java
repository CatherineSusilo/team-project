package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The items currently on the student's bench.
 */
public class Bench {

  private final List<LabItem> items = new ArrayList<>();

  /**
   * Puts an item on the bench.
   *
   * @param item the item to add
   */
  public void add(LabItem item) {
    items.add(item);
  }

  /**
   * Takes an item off the bench.
   *
   * @param id the id of the item to remove
   */
  public void remove(String id) {
    items.removeIf(item -> item.getId().equals(id));
  }

  /**
   * Finds an item by id.
   *
   * @param id the id to look for
   * @return the item, or empty if it is not on the bench
   */
  public Optional<LabItem> find(String id) {
    return items.stream().filter(item -> item.getId().equals(id)).findFirst();
  }

  public List<LabItem> getItems() {
    return List.copyOf(items);
  }
}