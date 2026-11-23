package entity;

import java.util.List;
import java.util.Optional;

/**
 * Every template the tray can offer in Lab 3.
 */
public final class ItemTemplates {

  private static final List<ItemTemplate> ALL = List.of(
      new ItemTemplate("conical_tube", ItemType.CONICAL_TUBE, null, 0, 15, 0),
      new ItemTemplate("test_tube", ItemType.TEST_TUBE, null, 0, 15, 0),
      new ItemTemplate("cylinder", ItemType.GRADUATED_CYLINDER, null, 0, 10, 0),
      new ItemTemplate("beaker", ItemType.BEAKER, null, 0, 100, 0),
      new ItemTemplate("watch_glass", ItemType.WATCH_GLASS, null, 0, 0, 0),
      new ItemTemplate("pipette", ItemType.PIPETTE, null, 0, 2, 0),
      new ItemTemplate("stir_rod", ItemType.TOOL, null, 0, 0, 0),
      new ItemTemplate("spatula", ItemType.TOOL, null, 0, 0, 0),
      new ItemTemplate("naoh_bottle", ItemType.DROPPER, Substances.NAOH, 3, 0, 0),
      new ItemTemplate("hcl_bottle", ItemType.DROPPER, Substances.HCL, 3, 0, 0),
      new ItemTemplate("water_bottle", ItemType.SOLVENT_BOTTLE, Substances.WATER, 0, 0, 0),
      new ItemTemplate("ether_bottle", ItemType.SOLVENT_BOTTLE, Substances.ETHER, 0, 0, 0),
      new ItemTemplate("unknown_vial", ItemType.VIAL, null, 0, 0, 0),
      new ItemTemplate("scale", ItemType.SCALE, null, 0, 0, 0),
      new ItemTemplate("ice_bath", ItemType.ICE_BATH, null, 0, 0, 0),
      new ItemTemplate("clamp_stand", ItemType.CLAMP_STAND, null, 0, 0, 0),
      new ItemTemplate("flask", ItemType.FILTRATION_FLASK, null, 0, 250, 0),
      new ItemTemplate("funnel", ItemType.BUCHNER_FUNNEL, null, 0, 0, 0),
      new ItemTemplate("vacuum_tap", ItemType.VACUUM_TAP, null, 0, 0, 0),
      new ItemTemplate("melting_point_app", ItemType.MELTING_POINT_APP, null, 0, 0, 0),
      new ItemTemplate("ir_spectrometer", ItemType.IR_SPECTROMETER, null, 0, 0, 0));

  private ItemTemplates() {
  }

  /**
   * Looks a template up.
   *
   * @param templateId the template's id
   * @return the template, or empty if there is none with that id
   */
  public static Optional<ItemTemplate> find(String templateId) {
    return ALL.stream().filter(template -> template.getTemplateId().equals(templateId))
        .findFirst();
  }

  /**
   * Lists every template.
   *
   * @return all the templates
   */
  public static List<ItemTemplate> all() {
    return ALL;
  }
}
