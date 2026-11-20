package entity;

import java.util.List;
import java.util.Optional;

/**
 * Catches one pipette being used for both solvents: it moved water earlier and now ether, or
 * the other way round.
 */
public final class CrossContaminationRule implements MistakeRule {

  @Override
  public Optional<String> check(Lab lab) {
    Optional<Transfer> last = lab.lastTransfer();
    if (!last.isPresent() || !isSolvent(last.get().getSubstance())) {
      return Optional.empty();
    }
    Transfer latest = last.get();
    List<Transfer> all = lab.getTransfers();
    // every transfer before the latest one
    for (Transfer earlier : all.subList(0, all.size() - 1)) {
      boolean samePipette = earlier.getToolId().equals(latest.getToolId());
      boolean otherSolvent = isSolvent(earlier.getSubstance())
          && earlier.getSubstance() != latest.getSubstance();
      if (samePipette && otherSolvent) {
        return Optional.of("Use one pipette per solvent.");
      }
    }
    return Optional.empty();
  }

  private boolean isSolvent(Substance substance) {
    return substance == Substances.WATER || substance == Substances.ETHER;
  }
}
