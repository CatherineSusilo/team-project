package adapter.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import usecase.ItemData;
import usecase.LabStateData;

/**
 * What the lab screen shows. Every lab presenter writes to this one view model.
 */
public class LabViewModel {

  /** Fired whenever the lab screen has to redraw. */
  public static final String STATE = "state";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private int stepIndex;
  private int stepCount;
  private List<String> stepTitles = List.of();
  private String title = "";
  private String instruction = "";
  private String message = "";
  private String status = "INCOMPLETE";
  private boolean nextLocked = true;
  private boolean prevLocked = true;
  private List<String> trayTemplates = List.of();
  private List<ItemState> items = List.of();
  private double waitMinutes;

  /**
   * Replaces everything the screen shows.
   *
   * @param state the lab as it is now
   */
  public void setState(LabStateData state) {
    this.stepIndex = state.getStepIndex();
    this.stepCount = state.getStepCount();
    this.stepTitles = List.copyOf(state.getStepTitles());
    this.title = state.getTitle();
    this.instruction = state.getInstruction();
    this.message = state.getMessage();
    this.status = state.getStatus().name();
    this.nextLocked = state.isNextLocked();
    this.prevLocked = state.isPrevLocked();
    this.trayTemplates = List.copyOf(state.getTrayTemplates());
    this.waitMinutes = state.getWaitMinutes();
    List<ItemState> shown = new ArrayList<>();
    for (ItemData item : state.getItems()) {
      shown.add(new ItemState(item.getId(), item.getType(), item.getLabel(), item.getX(),
          item.getY(), item.getLayerPhases(), item.getLayerVolumes(), item.hasSolid(),
          item.isSettled(), item.isCovered(), item.isCold(), item.getDisplay(), item.getPh()));
    }
    this.items = List.copyOf(shown);
  }

  /**
   * Changes only the message, for an action that was refused and changed nothing.
   *
   * @param message what to tell the student
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /** Tells the lab screen to redraw. */
  public void firePropertyChanged() {
    support.firePropertyChange(STATE, null, this);
  }

  /**
   * Registers a view to redraw when the state changes.
   *
   * @param listener the view
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public int getStepIndex() {
    return stepIndex;
  }

  public int getStepCount() {
    return stepCount;
  }

  public List<String> getStepTitles() {
    return stepTitles;
  }

  public String getTitle() {
    return title;
  }

  public String getInstruction() {
    return instruction;
  }

  public String getMessage() {
    return message;
  }

  public String getStatus() {
    return status;
  }

  public boolean isNextLocked() {
    return nextLocked;
  }

  public boolean isPrevLocked() {
    return prevLocked;
  }

  public List<String> getTrayTemplates() {
    return trayTemplates;
  }

  public List<ItemState> getItems() {
    return items;
  }

  public double getWaitMinutes() {
    return waitMinutes;
  }
}
