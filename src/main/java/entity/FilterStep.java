package entity;

import java.util.List;

/**
 * B3: collecting the solid on the Büchner funnel. Done once the solid is on the funnel and the
 * test tube has been rinsed out onto it with cold water (lab manual: 1 mL x 2).
 */
public class FilterStep extends LabStep {
  private final int rinses;           // UML: how many rinses the step asks for (2)

  /**
   * Creates the step.
   *
   * @param id the step's position in the lab, from 0
   * @param title a short name, e.g. "B3 Collect the solid"
   * @param instruction what the student is asked to do
   * @param hint what to say while the step is not done
   * @param trayTemplates the template ids the tray offers for this step
   * @param rinses how many cold-water rinses the step asks for, e.g. 2
   */
  public FilterStep(int id, String title, String instruction, String hint,
      List<String> trayTemplates, int rinses) {
    super(id, title, instruction, hint, trayTemplates, false);
    this.rinses = rinses;
  }

  public int getRinses() {
    return rinses;
  }

  @Override
  public boolean isComplete(Lab lab) {
    for (LabItem item : lab.getBench().getItems()) {
      if (item.getType() == ItemType.BUCHNER_FUNNEL) {
        BuchnerFunnel funnel = (BuchnerFunnel) item;
        return drug(funnel.getMixture()) > 0 && funnel.getRinses() >= rinses;
      }
    }
    return false;                     // no funnel on the bench yet
  }

  @Override
  public boolean expects(Action action) {
    // taking a pipette, moving things, and pouring (water into the tube, the tube onto the
    // funnel) all belong to collecting the solid
    return action.getKind() == ActionKind.SPAWN || action.getKind() == ActionKind.MOVE
        || action.getKind() == ActionKind.MOVE_LIQUID;
  }

  // Helper Function
  // How much of the drug is in a mixture (by B3 it's the only solid).
  private static double drug(Mixture mixture) {
    return mixture.amountOf(Substances.ASPIRIN) + mixture.amountOf(Substances.MEFENAMIC_ACID);
  }
}
