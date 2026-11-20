package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests Task A (NaOH moves the drug into the water) and Task B (HCl turns it back into a solid).
 */
class AcidBaseRuleTest {
  private final AcidBaseRule rule = new AcidBaseRule();

  /** The conical tube after the first mix: drug and vanillin dissolved in the ether. */
  private Mixture mixedTube() {
    Mixture tube = new Mixture();
    tube.add(Substances.ASPIRIN, 1.11);
    tube.add(Substances.VANILLIN, 0.3);
    tube.add(Substances.ETHER, 4);
    tube.add(Substances.WATER, 2);
    tube.mix(Mixture.FULL_MIX);
    return tube;
  }

  /** Adds a reagent the way the engine will: put it in, then let the rule react. */
  private void addReagent(Mixture tube, Substance reagent, double amount) {
    tube.add(reagent, amount);
    if (rule.appliesTo(tube, reagent)) {
      rule.apply(tube, reagent, amount);
    }
  }

  @Test
  void onlyNaohAndHclTriggerTheRule() {
    Mixture tube = new Mixture();
    assertTrue(rule.appliesTo(tube, Substances.NAOH));
    assertTrue(rule.appliesTo(tube, Substances.HCL));
    assertFalse(rule.appliesTo(tube, Substances.WATER));
    assertFalse(rule.appliesTo(tube, Substances.ASPIRIN));
  }

  @Test
  void naohMakesWaterBasic() {
    Mixture tube = mixedTube();
    addReagent(tube, Substances.NAOH, 1.5);
    assertEquals(AcidBaseRule.BASIC_PH, tube.getPh());
  }

  @Test
  void naohMovesDrugIntoWaterAndLeavesVanillinInEther() {
    Mixture tube = mixedTube();
    addReagent(tube, Substances.NAOH, 1.5);
    tube.mix(Mixture.FULL_MIX);

    Layer water = tube.getLayers().get(0);
    Layer ether = tube.getLayers().get(1);
    assertTrue(water.getDissolved().containsKey(Substances.ASPIRIN));
    assertTrue(ether.getDissolved().containsKey(Substances.VANILLIN));
    assertFalse(ether.getDissolved().containsKey(Substances.ASPIRIN));
  }

  @Test
  void hclTurnsDrugBackIntoSolid() {
    Mixture tube = mixedTube();
    addReagent(tube, Substances.NAOH, 1.5);
    tube.mix(Mixture.FULL_MIX);
    Mixture aqueousTube = tube.separateLayer(0);              

    addReagent(aqueousTube, Substances.HCL, 3.0);            
    aqueousTube.mix(Mixture.FULL_MIX);

    assertEquals(AcidBaseRule.ACIDIC_PH, aqueousTube.getPh());
    Layer water = aqueousTube.getLayers().get(0);
    assertTrue(water.getSolids().containsKey(Substances.ASPIRIN));
    assertFalse(water.getDissolved().containsKey(Substances.ASPIRIN));
  }
}
