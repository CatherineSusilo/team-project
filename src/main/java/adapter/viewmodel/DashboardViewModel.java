package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * What the dashboard shows. The LoadDashboard presenter writes it; the dashboard reads it.
 */
public class DashboardViewModel {
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private List<ModuleState> modules = List.of();
  private String message = "";

  /**
   * Replaces everything the dashboard shows.
   *
   * @param modules the module cards, in order
   * @param message a note to show above the cards, or empty for none
   */
  public void setState(List<ModuleState> modules, String message) {
    this.modules = List.copyOf(modules);
    this.message = message;
  }

  /** Tells the dashboard to redraw. */
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

  public List<ModuleState> getModules() {
    return modules;
  }

  public String getMessage() {
    return message;
  }
}
