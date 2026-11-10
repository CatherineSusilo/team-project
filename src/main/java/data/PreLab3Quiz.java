package data;

import entity.LabModule;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Pre-lab 3: Extraction Lab, with its pre-procedure questions taken from the lab manual.
 */
public final class PreLab3Quiz {

  private static final String OVERVIEW =
      "You're on the QA team at Lash Miller Inc. Two unlabelled batches of medicine, one "
          + "aspirin and one mefenamic acid, are both contaminated with vanillin. You'll get a "
          + "random unknown sample, separate the API from the vanillin, and identify which drug "
          + "it is.";

  private static final List<String> LEARNING_OBJECTIVES = List.of(
      "To separate a mixture of solids using liquid-liquid extraction technique",
      "To isolate a targeted solid compound following separation by using vacuum filtration",
      "To characterize an isolated solid compound using collected melting point and IR data, "
          + "along with provided NMR data",
      "To understand how acid/base chemistry can be used to alter solubility of compounds in "
          + "organic and aqueous solvents",
      "To understand how extractions are used in organic chemistry to isolate a compound");

  private static final String SEPARATION_SCENARIO =
      "To separate a mixture of vanillin and 2-chlorobenzoic acid, the mixture is dissolved in "
          + "diethyl ether, mixed with NaHCO₃(aq), and the aqueous layer is separated from "
          + "the ether layer.";

  /** Every species a student might put in either layer; used for both layer questions. */
  private static final List<String> SPECIES = List.of(
      "Water (H₂O)",
      "Sodium ion (Na⁺)",
      "Bicarbonate ion (HCO₃⁻)",
      "Chloride ion (Cl⁻)",
      "2-Chlorobenzoate ion",
      "2-Chlorobenzoic acid",
      "Vanillin",
      "Vanillin phenoxide ion",
      "Diethyl ether");

  private PreLab3Quiz() {
  }

  /**
   * Returns the Pre-lab 3 module, with its overview, learning objectives and pre-procedure
   * questions.
   *
   * @return the Pre-lab 3 module
   */
  public static LabModule module() {
    // TODO: load the student's real progress, and replace the placeholder due date.
    return new LabModule("Pre-lab 3: Extraction Lab", LocalDate.of(2026, 11, 30), OVERVIEW,
        LEARNING_OBJECTIVES, List.of("Part A", "Part B"),
        // Sample value; null means not started, which hides Continue.
        LocalDateTime.of(2026, 11, 5, 14, 30),
        questions());
  }

  /**
   * Returns the questions in the order the manual asks them.
   *
   * @return the pre-procedure questions
   */
  public static List<QuizQuestion> questions() {
    return List.of(
        new QuizQuestion(
            "Write the correctly balanced equation for the reaction between NaOH and "
                + "benzoic acid.",
            "C₆H₅COOH + NaOH",
            List.of("C₆H₅COONa", "H₂O", "NaCl", "CO₂"),
            Set.of(0, 1),
            "NaOH removes the acidic H from the carboxylic acid (–COOH), giving water and "
                + "sodium benzoate, a water-soluble salt. Every atom balances 1:1."),
        new QuizQuestion(
            "Write the correctly balanced equation for the reaction between HCl and "
                + "sodium benzoate.",
            "C₆H₅COONa + HCl",
            List.of("H₂O", "C₆H₅COOH", "NaOH", "NaCl"),
            Set.of(1, 3),
            "HCl gives the benzoate ion back its H, turning it into neutral benzoic acid (which "
                + "isn't water-soluble, so it precipitates) plus NaCl."),
        new QuizQuestion(
            SEPARATION_SCENARIO + " Which species does the aqueous layer contain? "
                + "Tick all that apply.",
            Type.MULTIPLE,
            SPECIES,
            Set.of(0, 1, 2, 4),
            "NaHCO₃ is basic enough to deprotonate the carboxylic acid (pKa ≈ 2.9), "
                + "making the water-soluble 2-chlorobenzoate ion with Na⁺ as its "
                + "counter-ion. Leftover HCO₃⁻ stays dissolved in the water too. "
                + "Vanillin's phenol (pKa ≈ 7.4) is too weak an acid for NaHCO₃ to "
                + "deprotonate, so vanillin stays out of the water."),
        new QuizQuestion(
            "Same separation: which species does the ether layer contain? "
                + "Tick all that apply.",
            Type.MULTIPLE,
            SPECIES,
            Set.of(6, 8),
            "Vanillin stays neutral because NaHCO₃ can't deprotonate its phenol, so it "
                + "remains dissolved in the diethyl ether."),
        new QuizQuestion(
            "Pick the skeletal structure of the organic component in the aqueous layer "
                + "(2-chlorobenzoate ion).",
            Type.IMAGE,
            List.of(
                "/structures/2-chlorobenzoic-acid.png",
                "/structures/4-chlorobenzoate.png",
                "/structures/2-chlorobenzoate.png",
                "/structures/3-chlorobenzoate.png"),
            Set.of(2),
            "2-Chlorobenzoate has the Cl next to the carboxylate (ortho) and has lost the acidic "
                + "H, so it carries a negative charge (COO⁻)."),
        new QuizQuestion(
            "Pick the skeletal structure of vanillin (ether layer).",
            Type.IMAGE,
            List.of(
                "/structures/isovanillin.png",
                "/structures/vanillin.png",
                "/structures/vanillin-phenoxide.png",
                "/structures/ethyl-vanillin.png"),
            Set.of(1),
            "Vanillin is a benzaldehyde with OH at position 4 and OCH₃ at position 3. "
                + "In the ether layer it is neutral, so the OH still has its H."),
        new QuizQuestion(
            "Pick the skeletal structure of diethyl ether (ether layer).",
            Type.IMAGE,
            List.of(
                "/structures/diethyl-ether.png",
                "/structures/ethanol.png",
                "/structures/tetrahydrofuran.png",
                "/structures/dimethyl-ether.png"),
            Set.of(0),
            "Diethyl ether is CH₃CH₂–O–CH₂CH₃: an oxygen with an "
                + "ethyl group on each side."));
  }
}
