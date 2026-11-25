package entity;

/**
 * What the simulation measured while the student worked. The notebook is separate: this holds
 * only results that later steps and the end screens need to read back.
 *
 * <p>It is saved with the lab, so an undo snapshot or a resumed lab keeps these numbers.
 */
public class LabRecord {

  private double startMassG;
  private double watchGlassMassG;
  private double finalMassG;
  private boolean unknownRecorded;
  private boolean guessCorrect;
  private String mixingPipetteId;

  /**
   * The mass of the unknown in the tube, set when A4 completes.
   *
   * @return grams, 0 until it is set
   */
  public double getStartMassG() {
    return startMassG;
  }

  public void setStartMassG(double startMassG) {
    this.startMassG = startMassG;
  }

  /**
   * The mass of the empty watch glass, set in B5.
   *
   * @return grams, 0 until it is set
   */
  public double getWatchGlassMassG() {
    return watchGlassMassG;
  }

  public void setWatchGlassMassG(double watchGlassMassG) {
    this.watchGlassMassG = watchGlassMassG;
  }

  /**
   * The mass of the dried solid, set in B6.
   *
   * @return grams, 0 until it is set
   */
  public double getFinalMassG() {
    return finalMassG;
  }

  public void setFinalMassG(double finalMassG) {
    this.finalMassG = finalMassG;
  }

  /**
   * Whether the student confirmed they wrote down the batch letter (A1).
   *
   * @return true once confirmed
   */
  public boolean isUnknownRecorded() {
    return unknownRecorded;
  }

  public void setUnknownRecorded(boolean unknownRecorded) {
    this.unknownRecorded = unknownRecorded;
  }

  /**
   * Whether the student picked the right drug in B7.
   *
   * @return true once they have
   */
  public boolean isGuessCorrect() {
    return guessCorrect;
  }

  public void setGuessCorrect(boolean guessCorrect) {
    this.guessCorrect = guessCorrect;
  }

  /**
   * The first pipette used to mix in A5, which "the same pipette" in A7 to A9 refers to.
   *
   * @return the pipette's id, or null if none has been used yet
   */
  public String getMixingPipetteId() {
    return mixingPipetteId;
  }

  public void setMixingPipetteId(String mixingPipetteId) {
    this.mixingPipetteId = mixingPipetteId;
  }
}
