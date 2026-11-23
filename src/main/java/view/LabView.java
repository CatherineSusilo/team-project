package view;

import adapter.controller.MoveItemController;
import adapter.controller.NavigateStepController;
import adapter.controller.SpawnItemController;
import adapter.viewmodel.LabViewModel;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The lab screen: the step's instructions on the left, the bench in the middle and the tray on
 * the right, with Prev and Next underneath. It redraws everything from the view model.
 */
public class LabView extends JPanel implements PropertyChangeListener {

  private final LabViewModel viewModel;
  private final InstructionPanel instruction = new InstructionPanel();
  private final BenchPanel bench;
  private final EquipmentPanel tray;
  private final NavBar nav;

  /**
   * Builds the lab screen.
   *
   * @param viewModel what the screen shows
   * @param navigateStep moves between steps
   * @param spawnItem puts a tray item on the bench
   * @param moveItem moves an item on the bench
   */
  public LabView(LabViewModel viewModel, NavigateStepController navigateStep,
      SpawnItemController spawnItem, MoveItemController moveItem) {
    super(new BorderLayout());
    this.viewModel = viewModel;
    DragDropManager manager = new DragDropManager(spawnItem, moveItem);
    this.bench = new BenchPanel(manager);
    manager.attach(bench);
    this.tray = new EquipmentPanel(manager);
    this.nav = new NavBar(() -> navigateStep.execute(false), () -> navigateStep.execute(true));
    add(instruction, BorderLayout.WEST);
    add(bench, BorderLayout.CENTER);
    add(tray, BorderLayout.EAST);
    add(nav, BorderLayout.SOUTH);
    viewModel.addPropertyChangeListener(this);
    redraw();
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      redraw();
    } else {
      SwingUtilities.invokeLater(this::redraw);
    }
  }

  private void redraw() {
    instruction.showStep(viewModel.getStepIndex(), viewModel.getStepCount(),
        viewModel.getTitle(), viewModel.getInstruction(), viewModel.getMessage(),
        viewModel.getStatus());
    bench.showItems(viewModel.getItems());
    tray.showTray(viewModel.getTrayTemplates());
    nav.showLocks(viewModel.isPrevLocked(), viewModel.isNextLocked());
  }
}
