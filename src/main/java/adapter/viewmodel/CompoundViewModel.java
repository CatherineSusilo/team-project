package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * What the compound search shows. The SearchCompound presenter writes it; the search view
 * reads it. Everything here is already display text.
 */
public class CompoundViewModel {
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private boolean found;
  private String name = "";
  private String formula = "";
  private String molecularWeight = "";
  private String meltingPoint = "";
  private String boilingPoint = "";
  private String signal = "";
  private List<String> hazardStatements = List.of();
  private byte[] structureImage;
  private String message = "";

  /**
   * Shows a compound that was found.
   *
   * @param name common name
   * @param formula molecular formula
   * @param molecularWeight e.g. "74.12 g/mol"
   * @param meltingPoint e.g. "-116.2 °C" or "Not available"
   * @param boilingPoint e.g. "34.6 °C" or "Not available"
   * @param signal "Danger", "Warning" or "Not available"
   * @param hazardStatements GHS hazard statements; empty if there are none
   * @param structureImage a PNG drawing of the structure, or null if there isn't one
   */
  public void setCompound(String name, String formula, String molecularWeight,
      String meltingPoint, String boilingPoint, String signal, List<String> hazardStatements,
      byte[] structureImage) {
    this.found = true;
    this.name = name;
    this.formula = formula;
    this.molecularWeight = molecularWeight;
    this.meltingPoint = meltingPoint;
    this.boilingPoint = boilingPoint;
    this.signal = signal;
    this.hazardStatements = List.copyOf(hazardStatements);
    this.structureImage = structureImage;
    this.message = "";
  }

  /**
   * Clears the last compound and shows why the search failed instead.
   *
   * @param message what went wrong
   */
  public void setError(String message) {
    this.found = false;
    this.name = "";
    this.formula = "";
    this.molecularWeight = "";
    this.meltingPoint = "";
    this.boilingPoint = "";
    this.signal = "";
    this.hazardStatements = List.of();
    this.structureImage = null;
    this.message = message;
  }

  /** Tells the search view to redraw. */
  public void firePropertyChanged() {
    support.firePropertyChange("state", null, this);
  }

  /**
   * Registers a view to redraw when the state changes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  /** Whether there's a compound to show; if not, show {@link #getMessage()}. */
  public boolean isFound() {
    return found;
  }

  public String getName() {
    return name;
  }

  public String getFormula() {
    return formula;
  }

  public String getMolecularWeight() {
    return molecularWeight;
  }

  public String getMeltingPoint() {
    return meltingPoint;
  }

  public String getBoilingPoint() {
    return boilingPoint;
  }

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

  public String getMessage() {
    return message;
  }
}
