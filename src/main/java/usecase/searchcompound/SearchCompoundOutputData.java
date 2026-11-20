package usecase.searchcompound;

import java.util.List;

/**
 * The properties of the compound that was found.
 */
public final class SearchCompoundOutputData {
  private final String name;
  private final String formula;
  private final double molecularWeight;
  private final String meltingPoint;
  private final String boilingPoint;
  private final String signal;
  private final List<String> hazardStatements;
  private final byte[] structureImage;

  /**
   * Creates the output.
   *
   * @param name common name
   * @param formula molecular formula
   * @param molecularWeight in g/mol
   * @param meltingPoint as reported, or null if unknown
   * @param boilingPoint as reported, or null if unknown
   * @param signal GHS signal word, or null if there's no GHS data
   * @param hazardStatements GHS hazard statements; empty if there are none
   * @param structureImage a PNG drawing of the structure, or null if there isn't one
   */
  public SearchCompoundOutputData(String name, String formula, double molecularWeight,
      String meltingPoint, String boilingPoint, String signal, List<String> hazardStatements,
      byte[] structureImage) {
    this.name = name;
    this.formula = formula;
    this.molecularWeight = molecularWeight;
    this.meltingPoint = meltingPoint;
    this.boilingPoint = boilingPoint;
    this.signal = signal;
    this.hazardStatements = List.copyOf(hazardStatements);
    this.structureImage = structureImage;
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

  /** A PNG drawing of the structure, or null if there isn't one. */
  public byte[] getStructureImage() {
    return structureImage;
  }
}
