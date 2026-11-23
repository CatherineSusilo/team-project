package usecase;

import entity.Vessel;

/**
 * What the UseItem use case needs to know about the items on the bench. Temporary: once the
 * Lab exists, the interactor gets the items through LabDataAccessInterface.
 */
public interface UseItemDataAccessInterface {

  /**
   * Returns the vessel with this id.
   *
   * @param itemId the item's id, e.g. "conical-1"
   * @return the vessel, or null if there is no vessel with that id
   */
  Vessel getVessel(String itemId);
}
