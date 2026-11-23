package entity;

/**
 * What a student can do to a single item with UseItem (and what an Action records), as in the
 * UML. UseItem handles LABEL and COVER so far; the rest get handled with their tickets.
 */
public enum Operation {
  // Write on the item's label, e.g. "A" on the conical tube (A2, A3).
  LABEL,
  // Put the inverted beaker on top, e.g. the ether cylinder (A3).
  COVER,
  // Zero the scale (A4).
  TARE,
  // Stir with the glass stir rod (B1).
  STIR,
  // Put filter paper in the Büchner funnel (B2).
  ADD_PAPER,
  // Turn the vacuum on (B3, B5).
  VACUUM_ON,
  // Turn the vacuum off.
  VACUUM_OFF,
  // Start the melting point apparatus (B7).
  START,
  // Run the IR spectrometer (B8).
  SCAN,
  // Name a peak on the IR spectrum (B8).
  LABEL_PEAK,
  // "I recorded it", e.g. the sample letter in the notebook (A1).
  CONFIRM,
  // Skip an optional step (B8).
  SKIP
}