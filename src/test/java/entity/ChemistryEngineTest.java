package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the whole extraction going through the engine: NaOH moves the drug into the water,
 * HCl turns it back into a solid, and vanillin stays in the ether.
 */
class ChemistryEngineTest {
  private final ChemistryEngine engine = new ChemistryEngine(List.of(new AcidBaseRule()));

  /** Task A up to the first mix: unknown, ether and water in the conical tube, mixed. */
  private Mixture conicalTube() {
    Mixture tube = new Mixture();
    engine.addReagent(tube, Substances.ASPIRIN, 1.11);
    engine.addReagent(tube, Substances.VANILLIN, 0.3);
    engine.addReagent(tube, Substances.ETHER, 4);
    engine.addReagent(tube, Substances.WATER, 2);
    tube.mix(Mixture.FULL_MIX);
    return tube;
  }

  @Test
  void naohRaisesThePh() {
    Mixture tube = conicalTube();
    engine.addReagent(tube, Substances.NAOH, 1.5);
    assertEquals(AcidBaseRule.BASIC_PH, tube.getPh());
  }

  @Test
  void waterDoesNotChangeThePh() {
    Mixture tube = conicalTube();
    engine.addReagent(tube, Substances.WATER, 2);
    assertEquals(7, tube.getPh());
  }

  @Test
  void wholeExtractionWorks() {
    Mixture tube = conicalTube();
    engine.addReagent(tube, Substances.NAOH, 1.5);          
    tube.mix(Mixture.FULL_MIX);
    Mixture aqueousTube = tube.separateLayer(0);             

    engine.addReagent(aqueousTube, Substances.HCL, 3.0);     
    aqueousTube.mix(Mixture.FULL_MIX);                       

    assertTrue(aqueousTube.getLayers().get(0).getSolids().containsKey(Substances.ASPIRIN));
    assertTrue(tube.getLayers().get(0).getDissolved().containsKey(Substances.VANILLIN));
  }
}
