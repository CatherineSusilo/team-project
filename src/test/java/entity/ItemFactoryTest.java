package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemFactoryTest {

  private final ItemFactory factory = new ItemFactory();

  private LabItem make(String templateId) {
    return factory.create(ItemTemplates.find(templateId).orElseThrow(), templateId + "-1");
  }

  @Test
  void glasswareBecomesARealVesselWithItsCapacityAndWeight() {
    for (String id : new String[] {"conical_tube", "test_tube", "cylinder", "beaker",
        "watch_glass", "flask"}) {
      assertTrue(make(id) instanceof Vessel, id);
    }
    Vessel beaker = (Vessel) make("beaker");
    assertEquals(100, beaker.getCapacity(), 0.0001);
    assertEquals(30, beaker.massG(), 0.0001);
  }

  @Test
  void pipettesBottlesAndTheScaleBecomeTheirOwnClasses() {
    assertTrue(make("pipette") instanceof PasteurPipette);
    assertTrue(make("naoh_bottle") instanceof StockBottle);
    assertTrue(make("ether_bottle") instanceof StockBottle);
    assertTrue(make("scale") instanceof Scale);
    assertTrue(make("melting_point_app") instanceof MeltingPointApp);
  }

  @Test
  void equipmentWithoutAClassYetStaysATemplateItem() {
    assertTrue(make("stir_rod") instanceof TemplateItem);
    assertTrue(make("unknown_vial") instanceof TemplateItem);
  }

  @Test
  void theThingsYouWeighHaveAWeightSoTareHasSomethingToDo() {
    assertEquals(6, ((Vessel) make("conical_tube")).massG(), 0.0001);
    assertEquals(20, ((Vessel) make("watch_glass")).massG(), 0.0001);
  }
}
