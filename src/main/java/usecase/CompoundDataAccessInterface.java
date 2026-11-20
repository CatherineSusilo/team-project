package usecase;

import entity.Compound;
import java.io.IOException;
import java.util.Optional;

/**
 * Looks up compounds by name.
 */
public interface CompoundDataAccessInterface {

  /**
   * Finds a compound by its name.
   *
   * @param name the compound's name, e.g. "diethyl ether"
   * @return the compound, or empty if nothing has that name
   * @throws IOException if the compound source can't be reached right now
   */
  Optional<Compound> findByName(String name) throws IOException;
}
