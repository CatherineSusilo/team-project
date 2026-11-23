package usecase.transfercontents;

/**
 * What the amount in a transfer is measured in.
 */
public enum TransferUnit {
  /** Millilitres, e.g. 4 mL of ether from a pipette. */
  ML,
  /** Drops from a dropper bottle, e.g. 10 drops of NaOH. */
  DROPS,
  /** Grams, e.g. 0.200 g of the unknown from the scale. */
  GRAMS,
  /** Pipette pumps, i.e. mixing what is already in the container. */
  MIXES
}
