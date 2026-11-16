package data;

import entity.Checkpoint;
import entity.QuizQuestion;
import entity.QuizQuestion.Type;
import java.util.List;
import java.util.Set;

/**
 * The checkpoint questions for Pre-lab 3: Extraction Lab, one after each procedure step, in
 * the order of the UML's LabSteps (step index 0 to 7).
 */
public final class PreLab3Checkpoints {

  private PreLab3Checkpoints() {
  }

  /**
   * Returns the checkpoints in step order.
   *
   * @return one checkpoint per procedure step
   */
  public static List<Checkpoint> checkpoints() {
    // TODO: check the wording against the lab manual, e.g. why vanillin stays in the ether
    //  layer with 3 M NaOH.
    return List.of(
        new Checkpoint(0, "Weigh the sample",
            new QuizQuestion(
                "You just weighed your unknown sample (about 0.200 g). Why record its exact "
                    + "mass before doing anything else?",
                Type.SINGLE,
                List.of(
                    "It is the starting amount used to work out percent recovery at the end",
                    "The balance needs the reading to warm up",
                    "The mass tells you which drug the sample is",
                    "It decides how much ether to add"),
                Set.of(0),
                "Percent recovery = (mass of isolated product / starting mass) x 100, so "
                    + "without the starting mass you can't say how much product you kept."),
            "Write the starting mass in your notebook now: it is the denominator of percent "
                + "recovery."),
        new Checkpoint(1, "Add the solvents",
            new QuizQuestion(
                "You added diethyl ether and water to the solid and two layers formed. "
                    + "Why, and which layer is on top?",
                Type.SINGLE,
                List.of(
                    "Ether and water don't mix, and ether is less dense, so it floats on top",
                    "Ether and water don't mix, and ether is denser, so it sinks to the bottom",
                    "Ether reacts with water to make a new compound that separates out",
                    "The solid is insoluble in both, so it splits them into layers"),
                Set.of(0),
                "Diethyl ether is non-polar and barely soluble in polar water, so they stay "
                    + "as two layers. Ether's density (about 0.71 g/mL) is lower than "
                    + "water's (1.00 g/mL), so the ether layer is on top."),
            "Ether = organic layer, on top. Water = aqueous layer, on the bottom."),
        new Checkpoint(2, "Add the base",
            new QuizQuestion(
                "You just added 10 drops of 3 M NaOH. What does the NaOH do to the carboxylic "
                    + "acid drug (RCOOH)?",
                Type.SINGLE,
                List.of(
                    "It removes the acidic H, giving the sodium carboxylate salt (RCOONa) "
                        + "and water",
                    "It adds an H, giving a form that is more soluble in ether",
                    "It breaks the drug down into smaller molecules",
                    "Nothing; NaOH is only there to dry the ether"),
                Set.of(0),
                "NaOH is a strong base: RCOOH + NaOH gives RCOONa + H₂O, the same reaction "
                    + "as NaOH with benzoic acid in the pre-lab. The sodium carboxylate is an "
                    + "ionic salt, so it is soluble in water instead of ether."),
            "RCOOH + NaOH gives RCOONa + H₂O. The ionic salt is what moves into the water "
                + "layer."),
        new Checkpoint(3, "Mix",
            new QuizQuestion(
                "You mixed the two layers about 30 times. Why? Tick all that apply.",
                Type.MULTIPLE,
                List.of(
                    "It increases the contact between the layers so the acid can be "
                        + "deprotonated and move into the water",
                    "It gives the acid-base transfer time to get close to equilibrium",
                    "It turns the ether and water into one permanent layer",
                    "It converts vanillin into the drug"),
                Set.of(0, 1),
                "Reactions and transfers between layers only happen at the interface, so "
                    + "mixing spreads one layer through the other. Ether and water still "
                    + "separate afterwards, and no new compound is made from vanillin."),
            "Mixing doesn't change the chemistry. It speeds up how fast the acid reaches the "
                + "base."),
        new Checkpoint(4, "Separate the layers",
            new QuizQuestion(
                "After mixing and letting the layers settle, where do the drug and the "
                    + "vanillin end up?",
                Type.SINGLE,
                List.of(
                    "Drug (as its salt) in the lower water layer; vanillin in the upper "
                        + "ether layer",
                    "Drug in the upper ether layer; vanillin (as its salt) in the lower "
                        + "water layer",
                    "Both in the upper ether layer",
                    "Both in the lower water layer"),
                Set.of(0),
                "The ionic sodium carboxylate is soluble in water (bottom). Vanillin is "
                    + "left behind in the diethyl ether (top)."),
            "Keep the aqueous layer: the drug is in there as a salt. Don't throw it away."),
        new Checkpoint(5, "Precipitate the product",
            new QuizQuestion(
                "You added HCl to the aqueous layer and chilled it on ice. Why does a solid "
                    + "appear? Tick all that apply.",
                Type.MULTIPLE,
                List.of(
                    "HCl gives the carboxylate its H back, making the neutral acid",
                    "The neutral acid is poorly soluble in water, especially when cold",
                    "HCl dissolves the drug so it crystallises out",
                    "The ice freezes the ether layer into a solid"),
                Set.of(0, 1),
                "HCl reverses the first reaction: carboxylate + HCl gives the neutral "
                    + "carboxylic acid + NaCl. The neutral acid can't dissolve in water the way "
                    + "the salt did, and cooling lowers its solubility further."),
            "Acid undoes the salt: neutral drug comes out of the water as a solid."),
        new Checkpoint(6, "Vacuum filter",
            new QuizQuestion(
                "You rinse the crystals in the Buchner funnel with only two small 1 mL "
                    + "portions of water. Why so little?",
                Type.SINGLE,
                List.of(
                    "The rinse washes off dissolved salts like NaCl, but a lot of water "
                        + "would also dissolve some of your product",
                    "Water is expensive in the lab",
                    "More water would make the crystals react with the filter paper",
                    "The rinse is only to wet the paper so it seals"),
                Set.of(0),
                "The product is only slightly soluble in water, but not insoluble. Small "
                    + "rinses remove the NaCl and leftover HCl while keeping losses, and so "
                    + "your percent recovery, as high as possible."),
            "Rinse small: wash off impurities without washing away product."),
        new Checkpoint(7, "Take the melting point",
            new QuizQuestion(
                "Your solid melts at 133 to 135 °C over a narrow range. Aspirin melts at about "
                    + "136 °C and mefenamic acid at about 230 °C. What is the best conclusion?",
                Type.SINGLE,
                List.of(
                    "It is aspirin, and it is fairly pure",
                    "It is mefenamic acid, and it is fairly pure",
                    "It is aspirin, but it is very impure",
                    "A melting point can't tell the two drugs apart"),
                Set.of(0),
                "The range is close to aspirin's melting point and far from mefenamic acid's. "
                    + "A narrow range suggests a pure sample; impurities such as leftover "
                    + "vanillin would lower and widen it."),
            "Melting point does two jobs: the value identifies the compound, the width "
                + "shows purity."));
  }
}
