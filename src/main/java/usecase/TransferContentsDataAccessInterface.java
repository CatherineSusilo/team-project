package usecase;

import entity.ItemType;
import entity.Mixture;
import entity.Substance;
import entity.UnknownSample;

/**
 * What the transfer use case needs to know about the items on the bench. The lab data class
 * implements it using the real items, so this use case never depends on them directly.
 */
public interface TransferContentsDataAccessInterface {

  /**
   * Returns what is inside a container.
   *
   * @param itemId the container's id, e.g. "conical_tube-1"
   * @return its mixture, or null if there is no such container
   */
  Mixture getMixture(String itemId);

  /**
   * Returns what a source holds, e.g. NaOH for the NaOH dropper bottle.
   *
   * @param itemId the source's id
   * @return the substance it pours, or null if it holds nothing to pour
   */
  Substance getSubstance(String itemId);

  /**
   * Returns what kind of item this is.
   *
   * @param itemId the item's id
   * @return its type, e.g. CONICAL_TUBE
   */
  ItemType getType(String itemId);

  /**
   * Returns the student's unknown if this item is the unknown sample jar.
   *
   * @param itemId the item's id
   * @return the unknown, or null if this item isn't the unknown sample
   */
  UnknownSample getUnknown(String itemId);

  /**
   * Returns how much a container holds.
   *
   * @param itemId the container's id
   * @return its capacity in mL, e.g. 2.0 for a pipette
   */
  double getCapacity(String itemId);
}
