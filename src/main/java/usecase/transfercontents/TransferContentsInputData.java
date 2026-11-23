package usecase.transfercontents;

/**
 * Input data for when a student transfers contents from one container to another.
 */
public final class TransferContentsInputData {
  private final String sourceId;
  private final String targetId;
  private final double amount;
  private final TransferUnit unit;

  /**
   * Creates the input.
   *
   * @param sourceId what the student is pouring from, e.g. the NaOH dropper bottle
   * @param targetId the container it goes into, e.g. the conical tube
   * @param amount how much, in the given unit
   * @param unit mL, drops, grams, or pipette pumps for mixing
   */
  public TransferContentsInputData(String sourceId, String targetId, double amount,
      TransferUnit unit) {
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.amount = amount;
    this.unit = unit;
  }

  public String getSourceId() {
    return sourceId;
  }

  public String getTargetId() {
    return targetId;
  }

  public double getAmount() {
    return amount;
  }

  public TransferUnit getUnit() {
    return unit;
  }
}
