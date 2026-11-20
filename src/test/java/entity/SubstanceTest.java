package entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests which layer each substance dissolves in. 
 */
class SubstanceTest {
  // pH of the water: 7 is neutral, NaOH makes it basic (high), HCl makes it acidic (low).
  private static final double NEUTRAL = 7;
  private static final double AFTER_NAOH = 13;
  private static final double AFTER_HCL = 1;

  @Test
  void aspirinDissolvesInEther() {
    assertTrue(Substances.ASPIRIN.isSolubleIn(Phase.ORGANIC, NEUTRAL));
  }

  @Test
  void aspirinDissolvesInBasicWater() {
    assertTrue(Substances.ASPIRIN.isSolubleIn(Phase.AQUEOUS, AFTER_NAOH));
  }

  @Test
  void aspirinNotInAcidicWater() {
    assertFalse(Substances.ASPIRIN.isSolubleIn(Phase.AQUEOUS, AFTER_HCL));
  }

  @Test
  void mefenamicAcidDissolvesInBasicWater() {
    assertTrue(Substances.MEFENAMIC_ACID.isSolubleIn(Phase.AQUEOUS, AFTER_NAOH));
  }

  @Test
  void mefenamicAcidNotInAcidicWater() {
    assertFalse(Substances.MEFENAMIC_ACID.isSolubleIn(Phase.AQUEOUS, AFTER_HCL));
  }

  @Test
  void vanillinStaysInEtherAfterNaoh() {
    assertFalse(Substances.VANILLIN.isSolubleIn(Phase.AQUEOUS, AFTER_NAOH));
    assertTrue(Substances.VANILLIN.isSolubleIn(Phase.ORGANIC, AFTER_NAOH));
  }

  @Test
  void waterDissolvesInWaterNotEther() {
    assertTrue(Substances.WATER.isSolubleIn(Phase.AQUEOUS, NEUTRAL));
    assertFalse(Substances.WATER.isSolubleIn(Phase.ORGANIC, NEUTRAL));
  }

  @Test
  void etherNeverDissolvesInWater() {
    assertFalse(Substances.ETHER.isSolubleIn(Phase.AQUEOUS, AFTER_NAOH));
  }

  @Test
  void nothingIsSolubleAsSolid() {
    assertFalse(Substances.ASPIRIN.isSolubleIn(Phase.SOLID, NEUTRAL));
    assertFalse(Substances.WATER.isSolubleIn(Phase.SOLID, NEUTRAL));
  }

  @Test
  void apisAreAcids() {
    assertTrue(Substances.ASPIRIN.isAcid());
    assertTrue(Substances.MEFENAMIC_ACID.isAcid());
  }

  @Test
  void othersAreNotAcids() {
    assertFalse(Substances.WATER.isAcid());
    assertFalse(Substances.ETHER.isAcid());
    assertFalse(Substances.NAOH.isAcid());
    assertFalse(Substances.HCL.isAcid());
    assertFalse(Substances.VANILLIN.isAcid());
  }

  @Test
  void aspirinNotInEtherAfterNaoh() {
    assertFalse(Substances.ASPIRIN.isSolubleIn(Phase.ORGANIC, AFTER_NAOH));
  }
}