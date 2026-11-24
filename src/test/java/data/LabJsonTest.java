package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import entity.Bench;
import entity.ItemType;
import entity.LabItem;
import entity.Layer;
import entity.PasteurPipette;
import entity.Phase;
import entity.Scale;
import entity.StockBottle;
import entity.Substances;
import entity.Vec2;
import entity.Vessel;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LabJsonTest {

  /** A stand-in item subclass with a field of its own. */
  static final class NotedItem extends LabItem {
    private final String note;

    NotedItem(String id, ItemType type, Vec2 position, String note) {
      super(id, type, position);
      this.note = note;
    }

    String getNote() {
      return note;
    }
  }

  private final LabJson labJson =
      new LabJson(Map.of(ItemType.TEST_TUBE, NotedItem.class, ItemType.BEAKER, NotedItem.class));

  @Test
  void benchKeepsEachItemSubclassAndItsFields() {
    Bench bench = new Bench();
    bench.addItem(new NotedItem("tube-1", ItemType.TEST_TUBE, new Vec2(0, 0), "first"),
        new Vec2(10, 20));
    bench.addItem(new NotedItem("beaker-1", ItemType.BEAKER, new Vec2(0, 0), "second"),
        new Vec2(30, 40));

    Bench copy = labJson.fromJson(labJson.toJson(bench), Bench.class);

    assertEquals(2, copy.getItems().size());
    NotedItem tube = (NotedItem) copy.getItems().get(0);
    assertEquals("tube-1", tube.getId());
    assertEquals(ItemType.TEST_TUBE, tube.getType());
    assertEquals(new Vec2(10, 20), tube.getPosition());
    assertEquals("first", tube.getNote());
    assertEquals("second", ((NotedItem) copy.getItems().get(1)).getNote());
  }

  @Test
  void substanceKeysComeBackAsTheSameConstants() {
    Layer layer = new Layer(Phase.AQUEOUS, 5,
        Map.of(Substances.NAOH, 2.5, Substances.WATER, 1.0),
        Map.of(Substances.ASPIRIN, 0.75));

    Layer copy = labJson.fromJson(labJson.toJson(layer), Layer.class);

    assertEquals(Phase.AQUEOUS, copy.getPhase());
    assertEquals(5, copy.getVolume());
    assertEquals(2.5, copy.getDissolved().get(Substances.NAOH));
    assertEquals(1.0, copy.getDissolved().get(Substances.WATER));
    assertEquals(0.75, copy.getSolids().get(Substances.ASPIRIN));
    assertSame(Substances.ASPIRIN, copy.getSolids().keySet().iterator().next());
  }

  @Test
  void substanceIsWrittenAsItsName() {
    Layer layer = new Layer(Phase.ORGANIC, 1, Map.of(Substances.ETHER, 1.0), Map.of());
    assertTrue(labJson.toJson(layer).contains("Diethyl ether"));
  }

  @Test
  void unknownSubstanceIsRejected() {
    String json = "{\"phase\":\"AQUEOUS\",\"volume\":1,\"dissolved\":{\"Nope\":1.0},"
        + "\"solids\":{}}";
    assertThrows(JsonParseException.class, () -> labJson.fromJson(json, Layer.class));
  }

  @Test
  void itemWithNoRegisteredClassIsRejected() {
    Bench bench = new Bench();
    bench.addItem(new NotedItem("tube-1", ItemType.TEST_TUBE, new Vec2(0, 0), "x"),
        new Vec2(1, 1));
    String json = labJson.toJson(bench);
    assertThrows(JsonParseException.class,
        () -> new LabJson(Map.of()).fromJson(json, Bench.class));
  }

  @Test
  void defaultConverterRestoresRealItems() {
    Vessel original = new Vessel("tube-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 5);
    original.setLabel("A");
    original.getMixture().add(Substances.WATER, 2);
    original.getMixture().add(Substances.ETHER, 3);
    original.getMixture().add(Substances.ASPIRIN, 0.5);
    original.getMixture().mix(30);
    PasteurPipette usedPipette = new PasteurPipette("pipette-1", new Vec2(0, 0));
    usedPipette.markUsed();
    Bench bench = new Bench();
    bench.addItem(original, new Vec2(1, 2));
    bench.addItem(usedPipette, new Vec2(3, 4));
    bench.addItem(new StockBottle("naoh-1", ItemType.DROPPER, new Vec2(0, 0), Substances.NAOH),
        new Vec2(5, 6));

    LabJson converter = new LabJson();
    Bench copy = converter.fromJson(converter.toJson(bench), Bench.class);

    Vessel tube = (Vessel) copy.getItems().get(0);
    PasteurPipette pipette = (PasteurPipette) copy.getItems().get(1);
    StockBottle bottle = (StockBottle) copy.getItems().get(2);
    assertEquals("A", tube.getLabel());
    assertEquals(5.0, tube.getMixture().totalVolume());
    assertEquals(original.massG(), tube.massG());
    assertFalse(pipette.isClean());
    assertSame(Substances.NAOH, bottle.getSubstance());
  }

  @Test
  void defaultConverterRestoresScale() {
    Vessel tube = new Vessel("tube-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15, 5);
    tube.getMixture().add(Substances.WATER, 2);
    Scale scale = new Scale("scale-1", new Vec2(0, 0));
    Bench bench = new Bench();
    bench.addItem(tube, new Vec2(1, 2));
    bench.addItem(scale, new Vec2(3, 4));
    scale.place("tube-1");
    scale.tare(bench);

    LabJson converter = new LabJson();
    Bench copy = converter.fromJson(converter.toJson(bench), Bench.class);

    Scale restored = (Scale) copy.getItems().get(1);
    assertEquals("tube-1", restored.getPlacedId());
    assertEquals(0.0, restored.reading(copy));
  }
}
