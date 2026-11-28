package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ice bath. Tubes and cylinders sit in it to cool (B2): the test tube for 10 minutes so
 * the drug comes out of solution, and both cylinders so the rinse water and ether are cold.
 * There's no temperature: anything put in is cold from then on.
 */
public class IceBath extends LabItem {
  private final List<String> immersed = new ArrayList<>();
  private final Map<String, Double> minutesImmersed = new HashMap<>();

  /**
   * Creates an empty ice bath.
   *
   * @param id a unique id, e.g. "ice_bath-1"
   * @param position where it sits on the bench
   */
  public IceBath(String id, Vec2 position) {
    super(id, ItemType.ICE_BATH, position);
  }

  /**
   * Puts a tube or cylinder in the bath. It's cold from now on.
   *
   * @param v what goes in
   */
  public void immerse(Vessel v) {
    if (!immersed.contains(v.getId())) {
      immersed.add(v.getId());
    }
    v.makeCold();
  }

  /**
   * Takes a tube or cylinder out (it was moved away). It keeps its minutes so far, so a test
   * tube that already had its 10 minutes stays done when it's moved.
   *
   * @param id what comes out
   */
  public void release(String id) {
    immersed.remove(id);
  }

  public boolean isImmersed(String id) {
    return immersed.contains(id);
  }

  /**
   * How long something has been in the bath.
   *
   * @param id the tube or cylinder
   * @return its lab minutes in the bath, 0 if it was never put in
   */
  public double minutesImmersed(String id) {
    return minutesImmersed.getOrDefault(id, 0.0);
  }

  /**
   * Lets time pass (the wait button): only what's in the bath right now gets the minutes.
   *
   * @param minutes how many lab minutes passed
   */
  public void advance(double minutes) {
    for (String id : immersed) {
      minutesImmersed.put(id, minutesImmersed(id) + minutes);
    }
  }
}
