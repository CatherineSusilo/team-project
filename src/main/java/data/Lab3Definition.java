package data;

import entity.Action;
import entity.ActionKind;
import entity.Bench;
import entity.CheckStep;
import entity.ItemFactory;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabStep;
import entity.Vec2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Builds Lab 3 (the extraction lab): its seventeen steps and the starting bench.
 *
 * <p>Every step is a stub for now: it is done once the equipment it needs is on the bench.
 * Each step's own rules replace its stub as its ticket is built.
 */
public final class Lab3Definition {

  private static final String NEED_IT = "Drag what you need from the tray onto the bench.";

  private Lab3Definition() {
  }

  /**
   * Builds a new lab at its first step.
   *
   * @return the lab, with the unknown sample's vial on the bench
   */
  public static Lab create() {
    Bench bench = new Bench();
    String vialId = bench.nextId("unknown_vial");
    bench.addItem(new ItemFactory().create(ItemTemplates.find("unknown_vial").orElseThrow(),
        vialId), new Vec2(90, 380));
    return new Lab(steps(), bench);
  }

  /**
   * Lists the steps, in order. This is also what a lab restored from a save gets its steps
   * back from.
   *
   * @return the seventeen steps
   */
  public static List<LabStep> steps() {
    List<LabStep> steps = new ArrayList<>();
    steps.add(step(steps, "A1 Record the unknown",
        "Record the batch letter of your unknown sample in your notebook.", List.of(),
        List.of()));
    steps.add(step(steps, "A2 Label the tubes",
        "Label the conical tube with your batch letter and the test tube \"aqueous\".",
        List.of("conical_tube", "test_tube"), List.of("conical_tube", "test_tube")));
    steps.add(step(steps, "A3 Set up the cylinders",
        "Label two graduated cylinders and measure about 6 mL each of water and ether. "
            + "Cover the ether.",
        List.of("cylinder", "water_bottle", "ether_bottle"), List.of("cylinder", "cylinder")));
    steps.add(step(steps, "A4 Weigh the sample",
        "Weigh 0.200 g of the unknown into the conical tube on the tared scale.",
        List.of("beaker", "scale"), List.of("beaker", "scale")));
    steps.add(step(steps, "A5 Dissolve in the solvents",
        "Add 4 mL ether and 2 mL water to the conical tube and mix with a pipette.",
        List.of("pipette"), List.of("pipette")));
    steps.add(step(steps, "A6 Add NaOH",
        "Add 10 drops of 3 M NaOH to the conical tube.",
        List.of("naoh_bottle"), List.of("naoh_bottle")));
    steps.add(step(steps, "A7 Mix",
        "Mix about 30 times with the same pipette until all the solid dissolves.",
        List.of(), List.of()));
    steps.add(step(steps, "A8 Separate the layers",
        "Let the layers settle, then move the aqueous layer to the test tube with a new "
            + "pipette.",
        List.of("pipette"), List.of("pipette", "pipette")));
    steps.add(step(steps, "A9 Second wash",
        "Add about 2 mL water, mix, let it settle, and combine the aqueous layers.",
        List.of(), List.of()));
    steps.add(step(steps, "B1 Acidify",
        "Add 20 drops of HCl to the test tube and stir 5 times.",
        List.of("hcl_bottle", "stir_rod"), List.of("hcl_bottle", "stir_rod")));
    steps.add(step(steps, "B2 Ice bath and filtration setup",
        "Cool the test tube in the ice bath for 10 minutes and set up the filtration.",
        List.of("ice_bath", "clamp_stand", "flask", "funnel"),
        List.of("ice_bath", "clamp_stand", "flask", "funnel")));
    steps.add(step(steps, "B3 Collect the solid",
        "Filter the solid onto the funnel and rinse it twice with 1 mL of cold water.",
        List.of(), List.of()));
    steps.add(step(steps, "B4 Ether rinse",
        "Rinse the solid with 2-3 drops of cold ether from a new pipette.",
        List.of("pipette"), List.of("pipette", "pipette", "pipette")));
    steps.add(step(steps, "B5 Dry the solid",
        "Dry the solid under vacuum for 10 minutes and weigh the empty watch glass.",
        List.of("vacuum_tap", "watch_glass"), List.of("vacuum_tap", "watch_glass")));
    steps.add(step(steps, "B6 Weigh the product",
        "Weigh the solid on the watch glass. The % recovery will show here.",
        List.of(), List.of()));
    steps.add(step(steps, "B7 Melting point",
        "Measure the melting range, then pick which drug you think the unknown is.",
        List.of("melting_point_app"), List.of("melting_point_app")));
    steps.add(optionalStep(steps, "B8 IR spectrum (optional)",
        "Scan the sample and label every peak, or skip this step.",
        List.of("ir_spectrometer"), List.of("ir_spectrometer")));
    return steps;
  }

  private static LabStep step(List<LabStep> before, String title, String instruction,
      List<String> tray, List<String> needed) {
    return new CheckStep(before.size(), title, instruction, NEED_IT, tray, false,
        hasOnBench(needed), Lab3Definition::isBenchAction);
  }

  private static LabStep optionalStep(List<LabStep> before, String title, String instruction,
      List<String> tray, List<String> needed) {
    return new CheckStep(before.size(), title, instruction, NEED_IT, tray, true,
        hasOnBench(needed), Lab3Definition::isBenchAction);
  }

  private static boolean isBenchAction(Action action) {
    return action.getKind() == ActionKind.SPAWN || action.getKind() == ActionKind.MOVE;
  }

  /** True once the bench holds at least as many of each template as listed. */
  private static Predicate<Lab> hasOnBench(List<String> needed) {
    Map<String, Integer> counts = new HashMap<>();
    for (String templateId : needed) {
      counts.merge(templateId, 1, Integer::sum);
    }
    return lab -> counts.entrySet().stream()
        .allMatch(entry -> lab.getBench().countOf(entry.getKey()) >= entry.getValue());
  }
}
