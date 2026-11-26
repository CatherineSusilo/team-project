package entity;

/**
 * Catches ether poured into the graduated cylinder labelled for water.
 */
public final class EtherInWaterCylinderRule implements MistakeRule {

  @Override
  public String check(Lab lab, Action action) {
    if (action.getKind() != ActionKind.ADD) {
      return null;
    }
    LabItem source = lab.getBench().getItem(action.getSourceId());
    LabItem target = lab.getBench().getItem(action.getTargetId());
    if (!(source instanceof StockBottle)
        || ((StockBottle) source).getSubstance() != Substances.ETHER
        || !(target instanceof Vessel) || target.getType() != ItemType.GRADUATED_CYLINDER) {
      return null;
    }
    if (((Vessel) target).labelMatches("water")) {
      return "Et2O goes in the cylinder labeled ether.";
    }
    return null;
  }
}
