package usecase;

import entity.Bench;
import entity.Scale;
import entity.Vessel;

/**
 * What the UseItem use case needs to know about the items on the bench. Temporary: once the
 * Lab exists, the interactor gets the items through LabDataAccessInterface and this goes away.
 */
public interface UseItemDataAccessInterface {

  /**
   * Returns the vessel with this id.
   *
   * @param itemId the item's id, e.g. "conical-1"
   * @return the vessel, or null if there is no vessel with that id
   */
  Vessel getVessel(String itemId);

  /**
   * Returns the scale with this id.
   *
   * @param itemId the item's id, e.g. "scale-1"
   * @return the scale, or null if that item isn't a scale
   */
  Scale getScale(String itemId);

  /**
   * Returns the bench, so the scale can find what's placed on it.
   *
   * @return the student's bench
   */
  Bench getBench();
}
