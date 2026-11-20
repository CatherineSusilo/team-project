package entity;

/**
 * Defines the Acid Base Rule (implementing Reaction Rule).
 */
public class AcidBaseRule implements ReactionRule {
  // pH after adding NaOH (strongly basic).
  public static final double BASIC_PH = 13;
  // pH after adding HCl (strongly acidic).
  public static final double ACIDIC_PH = 1;

  @Override
  public boolean appliesTo(Mixture mixture, Substance added) {
    return added == Substances.NAOH || added == Substances.HCL;   // only the base and the acid
  }

  @Override
  public void apply(Mixture mixture, Substance added, double amount) {
    if (added == Substances.NAOH) {
      mixture.setPh(BASIC_PH);                                    // NaOH → basic water
    } else if (added == Substances.HCL) {
      mixture.setPh(ACIDIC_PH);                                   // HCl → acidic water
    }
  }
}