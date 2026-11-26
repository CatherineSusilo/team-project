package entity;

/**
 * Catches pipette mix-ups. A pipette that has held both water and ether breaks "one pipette per
 * solvent"; it is checked after liquid moves into or out of a pipette, or after a pipette is
 * filled from a bottle. Mixing a tube with a pipette that has held liquid before breaks "use a
 * new pipette to mix" (A5); it is judged after the pipette pumped, so the mixing pipette has to
 * be marked before the action is recorded.
 *
 * <p>TODO (Lily): this only sees what a pipette holds right now. A pipette that drew water,
 * was emptied, then drew ether holds only ether, so it is not caught; that needs
 * {@code PasteurPipette} to remember which solvents it has drawn (set by TransferContents).
 * B4 (the ether rinse) also needs a new, dry pipette; that is not checked here either.
 */
public final class CrossContaminationRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() == ActionKind.MIX) {
      return checkMix(lab, action);
    }
    if (action.getKind() != ActionKind.MOVE_LIQUID && action.getKind() != ActionKind.ADD) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (holdsBothSolvents(source) || holdsBothSolvents(target)) {
      return "Use one pipette per solvent.";
    }
    return null;
  }

  private static String checkMix(Lab lab, Action action) {
    LabItem source = lab.getBench().getItem(action.getSourceId());
    if (source instanceof PasteurPipette && action.getTargetId() != null
        && !((PasteurPipette) source).canMix(action.getTargetId())) {
      return "Use a new pipette to mix.";
    }
    return null;
  }

  private static boolean holdsBothSolvents(LabItem item) {
    if (!(item instanceof PasteurPipette)) {
      return false;
    }
    boolean water = false;
    boolean ether = false;
    for (Layer layer : ((PasteurPipette) item).getMixture().getLayers()) {
      water = water || layer.getPhase() == Phase.AQUEOUS && layer.getVolume() > 0;
      ether = ether || layer.getPhase() == Phase.ORGANIC && layer.getVolume() > 0;
    }
    return water && ether;
  }
}
