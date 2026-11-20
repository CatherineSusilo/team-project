package entity;

import java.util.List;

/**
 * A chemical compound and the properties a student needs when choosing reagents.
 */
public final class Compound {
  private final String name;
  private final String formula;
  private final double molecularWeight;
  private final String meltingPoint;
  private final String boilingPoint;
  private final String signal;
  private final List<String> hazardStatements;
  private final byte[] structureImage;

  /**
   * Creates a compound.
   *
   * @param name common name, e.g. "Diethyl Ether"
   * @param formula molecular formula, e.g. "C4H10O"
   * @param molecularWeight in g/mol
   * @param meltingPoint as reported, e.g. "-116.2 °C", or null if unknown
   * @param boilingPoint as reported, e.g. "34.6 °C", or null if unknown
   * @param signal GHS signal word ("Danger" or "Warning"), or null if there's no GHS data
   * @param hazardStatements GHS hazard statements, e.g. "H224: Extremely flammable liquid and
   *     vapor"; empty if there are none
   * @param structureImage a PNG drawing of the structure, or null if there isn't one
   */
  public Compound(String name, String formula, double molecularWeight, String meltingPoint,
      String boilingPoint, String signal, List<String> hazardStatements, byte[] structureImage) {
    this.name = name;
    this.formula = formula;
    this.molecularWeight = molecularWeight;
    this.meltingPoint = meltingPoint;
    this.boilingPoint = boilingPoint;
    this.signal = signal;
    this.hazardStatements = List.copyOf(hazardStatements);
    // Arrays can be changed from outside, so keep our own copy.
    this.structureImage = structureImage == null ? null : structureImage.clone();
  }

  public String getName() {
    return name;
  }

  public String getFormula() {
    return formula;
  }

  public double getMolecularWeight() {
    return molecularWeight;
  }

  /** The melting point as reported, or null if unknown. */
  public String getMeltingPoint() {
    return meltingPoint;
  }

  /** The boiling point as reported, or null if unknown. */
  public String getBoilingPoint() {
    return boilingPoint;
  }

  /** "Danger" or "Warning", or null if there's no GHS data. */
  public String getSignal() {
    return signal;
  }

  public List<String> getHazardStatements() {
    return hazardStatements;
  }

  /** A copy of the PNG drawing of the structure, or null if there isn't one. */
  public byte[] getStructureImage() {
    return structureImage == null ? null : structureImage.clone();
  }
}
