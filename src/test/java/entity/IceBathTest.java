package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the ice bath: what goes in is cold, and only what's in it gets the minutes.
 */
class IceBathTest {
  private final IceBath bath = new IceBath("ice_bath-1", new Vec2(0, 0));
  private final Vessel testTube =
      new Vessel("test_tube-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);
  private final Vessel cylinder =
      new Vessel("cylinder-1", ItemType.GRADUATED_CYLINDER, new Vec2(0, 0), 10, 25);

  @Test
  void anythingPutInIsCold() {
    bath.immerse(testTube);
    assertTrue(testTube.isCold());                         // the cold badge
    assertTrue(bath.isImmersed("test_tube-1"));
  }

  @Test
  void holdsSeveralAtOnce() {
    bath.immerse(testTube);
    bath.immerse(cylinder);
    bath.advance(10);
    assertEquals(10, bath.minutesImmersed("test_tube-1"));
    assertEquals(10, bath.minutesImmersed("cylinder-1"));
  }

  @Test
  void minutesAddUp() {
    bath.immerse(testTube);
    bath.advance(4);
    bath.advance(6);
    assertEquals(10, bath.minutesImmersed("test_tube-1"));
  }

  @Test
  void somethingNotInTheBathGetsNoMinutes() {
    bath.advance(10);
    assertEquals(0, bath.minutesImmersed("test_tube-1"));
  }

  @Test
  void releasedItIsOutButStaysCold() {
    bath.immerse(testTube);
    bath.advance(4);
    bath.release("test_tube-1");                           // lifted out
    assertFalse(bath.isImmersed("test_tube-1"));
    assertEquals(0, bath.minutesImmersed("test_tube-1"));  // starts over if it goes back in
    assertTrue(testTube.isCold());
  }

  @Test
  void puttingItInTwiceCountsOnce() {
    bath.immerse(testTube);
    bath.immerse(testTube);
    bath.advance(10);
    assertEquals(10, bath.minutesImmersed("test_tube-1"));
  }
}
