package view;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * What the tray and bench show for each template id: a name and a sprite picture. A template
 * without a sprite is drawn as a labelled box.
 */
final class SpriteCatalog {

  private static final Map<String, String[]> ENTRIES = new HashMap<>();
  private static final Map<String, Image> IMAGES = new HashMap<>();

  static {
    add("conical_tube", "Conical tube", "conical-tube--unlabeled");
    add("test_tube", "Test tube", "test-tube--emptied");
    add("cylinder", "Graduated cylinder", "graduated-cylinder--unlabeled");
    add("beaker", "Beaker", null);
    add("watch_glass", "Watch glass", "tools--watch-glass");
    add("pipette", "Pasteur pipette", "pasteur-pipette--empty");
    add("stir_rod", "Stir rod", "stir-rod--resting");
    add("spatula", "Spatula", "tools--spatula");
    add("naoh_bottle", "NaOH (3 M)", "dropper-bottle--naoh");
    add("hcl_bottle", "HCl (3 M)", "dropper-bottle--hcl");
    add("water_bottle", "Distilled water", "material--distilled-water");
    add("ether_bottle", "Diethyl ether", "material--diethyl-ether-stock");
    add("unknown_vial", "Unknown sample", "material--unknown-sample");
    add("scale", "Balance", "balance--tared");
    add("ice_bath", "Ice bath", "ice-bath--default");
    add("clamp_stand", "Clamp stand", "filtration--clamp-stand");
    add("flask", "Filtration flask", "filtration--flask-empty");
    add("funnel", "Büchner funnel", "buchner-funnel--empty");
    add("vacuum_tap", "Vacuum tap", "vacuum-tap--off");
    add("melting_point_app", "Melting point app", "melting-point--idle");
    add("ir_spectrometer", "IR spectrometer", "ir-spectrometer--idle");
  }

  private SpriteCatalog() {
  }

  private static void add(String templateId, String name, String sprite) {
    ENTRIES.put(templateId, new String[] {name, sprite});
  }

  /**
   * The name shown for a template.
   *
   * @param templateId the template's id
   * @return its display name, or the id itself if it is unknown
   */
  static String nameOf(String templateId) {
    String[] entry = ENTRIES.get(templateId);
    return entry == null ? templateId : entry[0];
  }

  /**
   * The sprite picture for a template.
   *
   * @param templateId the template's id
   * @return the picture, or null if it has none or it can't be read
   */
  static Image spriteOf(String templateId) {
    String[] entry = ENTRIES.get(templateId);
    if (entry == null || entry[1] == null) {
      return null;
    }
    return IMAGES.computeIfAbsent(entry[1], SpriteCatalog::load);
  }

  private static Image load(String sprite) {
    try (InputStream in = SpriteCatalog.class.getResourceAsStream(
        "/sprites/" + sprite + ".png")) {
      return in == null ? null : ImageIO.read(in);
    } catch (IOException e) {
      return null;
    }
  }
}
