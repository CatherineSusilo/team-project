package usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import data.Lab3Definition;
import entity.ApiType;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabItem;
import entity.Range;
import entity.Scale;
import entity.Substances;
import entity.UnknownSample;
import entity.Vec2;
import entity.Vessel;
import org.junit.jupiter.api.Test;

class LabStateMapperTest {

  private static Lab lab() {
    return Lab3Definition.create(new UnknownSample('A', ApiType.ASPIRIN));
  }

  private static <T> T put(Lab lab, String templateId) {
    String id = lab.getBench().nextId(templateId);
    LabItem item = new ItemFactory().create(
        ItemTemplates.find(templateId).orElseThrow(), id);
    lab.getBench().addItem(item, new Vec2(50, 50));
    @SuppressWarnings("unchecked")
    T typed = (T) item;
    return typed;
  }

  private static ItemData find(LabStateData state, String id) {
    return state.getItems().stream().filter(item -> item.getId().equals(id)).findFirst()
        .orElseThrow();
  }

  @Test
  void listsEveryStepTitleSoTheManualCanShowThemAll() {
    LabStateData state = LabStateMapper.from(lab());

    assertEquals(17, state.getStepTitles().size());
    assertTrue(state.getStepTitles().get(0).startsWith("A1"));
  }

  @Test
  void aVesselsLabelShowsOnItsItem() {
    Lab lab = lab();
    Vessel tube = put(lab, "conical_tube");
    tube.setLabel("A");

    assertEquals("A", find(LabStateMapper.from(lab), "conical_tube-1").getLabel());
  }

  @Test
  void aPouredVesselReportsItsLayersCoverAndColdness() {
    Lab lab = lab();
    Vessel tube = put(lab, "conical_tube");
    tube.getMixture().add(Substances.WATER, 2);
    tube.cover();
    tube.makeCold();

    ItemData item = find(LabStateMapper.from(lab), "conical_tube-1");

    assertEquals(1, item.getLayerPhases().size());
    assertEquals(2, item.getLayerVolumes().get(0), 0.0001);
    assertFalse(item.hasSolid());
    assertTrue(item.isCovered());
    assertTrue(item.isCold());
  }

  @Test
  void aScaleReportsItsDisplay() {
    Lab lab = lab();
    Vessel beaker = put(lab, "beaker");
    Scale scale = put(lab, "scale");
    scale.place(beaker.getId());

    assertEquals("30.000", find(LabStateMapper.from(lab), "scale-1").getDisplay());
  }

  @Test
  void meltingRangeIsBlankUntilMeasured() {
    assertEquals("", LabStateMapper.from(lab()).getMeltingRange());
  }

  @Test
  void showsTheMeasuredMeltingRange() {
    Lab lab = lab();
    lab.getRecord().setMeltingRange(new Range(125, 132));
    assertEquals("125-132 °C", LabStateMapper.from(lab).getMeltingRange());
  }
}
