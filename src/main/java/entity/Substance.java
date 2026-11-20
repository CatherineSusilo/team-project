package entity;

/**
 * A chemical used in the lab (e.g. water, ether, NaOH, aspirin) and its properties. Substances
 * never change and are stored in a layer.
 */
public final class Substance {
  private final String name;
  private final String formula;
  private final double molarMass;
  private final double pka;
  private final Range meltingRange;
  private final Phase phase;

  /**
   * For acids, creates a substance with its name, formula, molar mass, pKa, and melting range.
   */
  public Substance(String name, String formula, double molarMass,
      double pka, Range meltingRange, Phase phase) {
    this.name = name;
    this.formula = formula;
    this.molarMass = molarMass;
    this.pka = pka;
    this.meltingRange = meltingRange;
    this.phase = phase;
  }

  /**
   * For non-acids, creates a substance with its name, formula,
   * molar mass, and melting range. pKa is NaN.
   */
  public Substance(String name, String formula, double molarMass,
      Range meltingRange, Phase phase) {
    this.name = name;
    this.formula = formula;
    this.molarMass = molarMass;
    this.pka = Double.NaN;
    this.meltingRange = meltingRange;
    this.phase = phase;
  }

  public String getName() {
    return name;
  }

  public String getFormula() {
    return formula;
  }

  public double getMolarMass() {
    return molarMass;
  }

  public double getPka() {
    return pka;
  }

  public Range getMeltingRange() {
    return meltingRange;
  }

  public Phase getPhase() {
    return phase;
  }

  /**
   * Whether this substance dissolves in a phase at a given pH. An acid moves into water once a
   * base has made the water basic (pH above 7 and above its pKa), because it becomes its salt;
   * the salt then no longer dissolves in ether.
   *
   * @param p the phase to check
   * @param ph the pH of the mixture
   * @return true if it dissolves there
   */
  public boolean isSolubleIn(Phase p, double ph) {
    boolean isSalt = isAcid() && ph > 7 && ph > pka;  
    if (isSalt) {
      return p == Phase.AQUEOUS;                      
    }
    return p == phase;                                
  }

  /**
   * Whether this substance is an acid a base can turn into a salt.
   *
   * @return true if it has a pKa
   */
  public boolean isAcid() {
    return !Double.isNaN(pka);
  }
}