package entity;

/**
 * One liquid the student moved into a vessel, kept so mistake rules can look back at it.
 */
public final class Transfer {
  private final String toolId;
  private final String target;
  private final Substance substance;
  private final double amount;

  /**
   * Creates a record of a transfer.
   *
   * @param toolId the pipette or dropper used
   * @param target the name of the vessel it went into, e.g. "conical tube"
   * @param substance what was added
   * @param amount how much was added (mL for water and ether, otherwise mmol)
   */
  public Transfer(String toolId, String target, Substance substance, double amount) {
    this.toolId = toolId;
    this.target = target;
    this.substance = substance;
    this.amount = amount;
  }

  public String getToolId() {
    return toolId;
  }

  public String getTarget() {
    return target;
  }

  public Substance getSubstance() {
    return substance;
  }

  public double getAmount() {
    return amount;
  }
}
