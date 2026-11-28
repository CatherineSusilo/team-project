package entity;

/**
 * The unknown a student gets at the start of the lab (Task A, step 1). The batch letter is
 * written on the conical tube; which drug (API) is inside stays hidden until the student
 * guesses it at the end.
 */
public final class UnknownSample {
  // Share of every scoop that is vanillin, the impurity (the manual gives no ratio).
  public static final double VANILLIN_FRACTION = 0.20;
  
  private final char batch;     // 'A' or 'B', written on the conical tube
  private final ApiType api;    // hidden: the student finds out with checkGuess

  /**
   * Creates an unknown sample.
   *
   * @param batch the batch letter, 'A' or 'B'
   * @param api which drug is in the unknown
   */
  public UnknownSample(char batch, ApiType api) {
    this.batch = batch;
    this.api = api;
  }

  public char getBatch() {
    return batch;
  }

  /**
   * Adds one spatula scoop of the unknown to a container.
   *
   * @param tube the container it goes into, e.g. the conical tube
   * @param grams how much was scooped
   */
  public void addTo(Mixture tube, double grams) {
    Substance drug = drugOf(api);
    double vanillinGrams = grams * VANILLIN_FRACTION;    // e.g. 0.040 g of 0.200 g
    tube.add(drug, grams - vanillinGrams);
    tube.add(Substances.VANILLIN, vanillinGrams);  
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

  /**
   * The substance a kind of drug is, e.g. to look it up in a mixture or read its melting point.
   *
   * @param api the kind of drug
   * @return aspirin or mefenamic acid
   */
  public static Substance drugOf(ApiType api) {
    if (api == ApiType.ASPIRIN) {
      return Substances.ASPIRIN;
    }
    return Substances.MEFENAMIC_ACID;
  }
}
