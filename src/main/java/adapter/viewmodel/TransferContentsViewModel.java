package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * What the bench shows after a transfer: the container that changed and a message. The bench
 * view listens to it and redraws that container's layers.
 */
public class TransferContentsViewModel {
  /** Fired when a transfer finishes, successful or not. */
  public static final String RESULT = "result";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private boolean success;
  private String targetId = "";
  private List<String> layerPhases = List.of();
  private List<Double> layerVolumes = List.of();
  private boolean hasSolid;
  private double ph = 7;
  private String message = "";

  /**
   * Records a container after a successful transfer.
   *
   * @param targetId the container that changed
   * @param layerPhases each layer's phase, bottom first
   * @param hasSolid whether there is undissolved solid in it
   * @param ph its pH
   * @param message what happened
   */
  public void setResult(String targetId, List<String> layerPhases,
      boolean hasSolid, double ph, String message) {
    this.success = true;
    this.targetId = targetId;
    this.layerPhases = List.copyOf(layerPhases);
    this.hasSolid = hasSolid;
    this.ph = ph;
    this.message = message;
  }

  /**
   * Records that the transfer didn't happen. The containers keep what they had.
   *
   * @param message why, e.g. "HCl goes into the aqueous test tube."
   */
  public void setError(String message) {
    this.success = false;
    this.message = message;
  }

  /**
   * Tells the bench to redraw.
   */
  public void firePropertyChanged() {
    support.firePropertyChange(RESULT, null, this);
  }

  /**
   * Registers a view to redraw when a transfer finishes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getTargetId() {
    return targetId;
  }

  public List<String> getLayerPhases() {
    return layerPhases;
  }

  public List<Double> getLayerVolumes() {
    return layerVolumes;
  }

  public boolean hasSolid() {
    return hasSolid;
  }

  public double getPh() {
    return ph;
  }

  public String getMessage() {
    return message;
  }
}
