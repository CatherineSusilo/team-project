package entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The ice bath. Tubes and cylinders sit in it to cool (B2): the test tube for 10 minutes so
 * the drug comes out of solution, and both cylinders so the rinse water and ether are cold.
 * There's no temperature: anything put in is cold from then on.
 */
public class IceBath extends LabItem {
  private final Map<String, Double> minutesImmersed = new HashMap<>();  // what's in it -> minutes

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
    minutesImmersed.putIfAbsent(v.getId(), 0.0);
    v.makeCold();
  }

  /**
   * Takes a tube or cylinder out (it was moved away). It stays cold, but its minutes start
   * over if it goes back in.
   *
   * @param id what comes out
   */
  public void release(String id) {
    minutesImmersed.remove(id);
  }

  public boolean isImmersed(String id) {
    return minutesImmersed.containsKey(id);
  }

  /**
   * How long something has been in the bath.
   *
   * @param id the tube or cylinder
   * @return its lab minutes in the bath, 0 if it isn't in it
   */
  public double minutesImmersed(String id) {
    return minutesImmersed.getOrDefault(id, 0.0);
  }

  /**
   * Lets time pass (the timer bar or its Skip button): everything in the bath gets the minutes.
   *
   * @param minutes how many lab minutes passed
   */
  public void advance(double minutes) {
    minutesImmersed.replaceAll((id, soFar) -> soFar + minutes);
  }
}
