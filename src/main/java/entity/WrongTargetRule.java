package entity;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Catches a reagent added to the wrong kind of container (HCl into the conical tube, say). The
 * table is the lab manual's: which containers each stock bottle may be poured into.
 */
public final class WrongTargetRule implements MistakeRule {

  private static final Set<ItemType> SOLVENT_TARGETS =
      EnumSet.of(ItemType.CONICAL_TUBE, ItemType.GRADUATED_CYLINDER, ItemType.PIPETTE);

  private static final Map<Substance, Set<ItemType>> ALLOWED = Map.of(
      Substances.NAOH, EnumSet.of(ItemType.CONICAL_TUBE),
      Substances.HCL, EnumSet.of(ItemType.TEST_TUBE),
      Substances.WATER, SOLVENT_TARGETS,
      Substances.ETHER, SOLVENT_TARGETS);

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (!(source instanceof StockBottle) || target == null) {
      return null;
    }
    Substance added = ((StockBottle) source).getSubstance();
    Set<ItemType> allowed = ALLOWED.get(added);
    if (allowed == null || allowed.contains(target.getType())) {
      return null;
    }
    return whereItGoes(added);
  }

  private static String whereItGoes(Substance added) {
    if (added == Substances.NAOH) {
      return "NaOH goes into the conical tube.";
    } else if (added == Substances.HCL) {
      return "HCl goes into the aqueous test tube.";
    }
    return added.getName() + " goes into the conical tube or its graduated cylinder.";
  }
}
