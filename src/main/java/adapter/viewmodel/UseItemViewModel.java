package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * What the bench shows after an item is used: its label, or an error. A stand-in
 * until the shared LabViewModel exists (same as TransferContentsViewModel).
 */
public class UseItemViewModel {
  /** Fired when using an item finishes, successful or not. */
  public static final String RESULT = "result";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private boolean success;
  private String itemId = "";
  private String label = "";
  private String message = "";

  /**
   * Records an item after it was used.
   *
   * @param itemId the item that changed
   * @param label what its label says now
   * @param message what happened
   */
  public void setResult(String itemId, String label, String message) {
    this.success = true;
    this.itemId = itemId;
    this.label = label;
    this.message = message;
  }

  /**
   * Records that it didn't work. The item is unchanged.
   *
   * @param message why, e.g. "Write something on the label."
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
   * Registers a view to redraw when using an item finishes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getItemId() {
    return itemId;
  }

  public String getLabel() {
    return label;
  }

  public String getMessage() {
    return message;
  }
}
