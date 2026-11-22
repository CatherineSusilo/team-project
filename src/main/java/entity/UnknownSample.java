package entity;

/**
 * The unknown a student gets at the start of the lab (Task A, step 1). The batch letter is
 * written on the conical tube; which drug (API) is inside stays hidden until the student
 * guesses it at the end.
 */
public final class UnknownSample {
  private final char batch;     // 'A' or 'B', written on the conical tube
  private final ApiType api;    // hidden: the student finds out with checkGuess
  private final double mass;   

  /**
   * Creates an unknown sample.
   *
   * @param batch the batch letter, 'A' or 'B'
   * @param api which drug is in the unknown
   * @param mass how many grams of the unknown
   */
  public UnknownSample(char batch, ApiType api, double mass) {
    this.batch = batch;
    this.api = api;
    this.mass = mass;
  }

  /**
   * Puts the unknown into a new mixture.
   *
   * @return a mixture holding the drug, still undissolved
   */
  public Mixture toMixture() {
    Substance drug;
    if (api == ApiType.ASPIRIN) {
      drug = Substances.ASPIRIN;
    } else {
      drug = Substances.MEFENAMIC_ACID;
    }
    Mixture mixture = new Mixture();
    mixture.add(drug, mass);    // sits as powder until solvent is added and mixed
    return mixture;
  }

  /**
   * Checks the student's guess of which drug is in the unknown.
   *
   * @param guess the drug the student thinks it is
   * @return true if the guess is right
   */
  public boolean checkGuess(ApiType guess) {
    return guess == api;
  }
}
