package view;

import adapter.controller.MoveItemController;
import adapter.controller.NavigateStepController;
import adapter.controller.SpawnItemController;
import adapter.viewmodel.LabViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The lab screen: the Lab Manual on the left with Exit and Continue under it, and the fume hood
 * in the middle with the equipment tray along its counter. The notebook sits beside it, added by
 * whoever shows this screen. It redraws everything from the view model.
 */
public class LabView extends JPanel implements PropertyChangeListener {

  private final LabViewModel viewModel;
  private final InstructionPanel instruction = new InstructionPanel();
  private final BenchPanel bench;
  private final EquipmentPanel tray;
  private final NavBar nav;
  private Runnable onExit = () -> { };

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
    this.nav = new NavBar(() -> navigateStep.execute(false), () -> navigateStep.execute(true),
        () -> onExit.run());

    JPanel manual = new JPanel(new BorderLayout());
    manual.setBackground(new Color(0xFBFBFB));
    manual.setPreferredSize(new Dimension(320, 0));
    manual.add(instruction, BorderLayout.CENTER);
    manual.add(nav, BorderLayout.SOUTH);

    JLabel reminder = new JLabel("Everything Is Done In The Fume Hood!");
    reminder.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE));
    reminder.setOpaque(true);
    reminder.setBackground(new Color(0xF2F2F2));
    reminder.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    JPanel hood = new JPanel(new BorderLayout());
    hood.add(reminder, BorderLayout.NORTH);
    hood.add(bench, BorderLayout.CENTER);
    hood.add(tray, BorderLayout.SOUTH);

    add(manual, BorderLayout.WEST);
    add(hood, BorderLayout.CENTER);
    viewModel.addPropertyChangeListener(this);
    redraw();
  }

  /**
   * Says what Exit Module does.
   *
   * @param action runs when the student clicks Exit Module
   */
  public void setOnExit(Runnable action) {
    this.onExit = action;
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
    instruction.showStep(viewModel.getStepIndex(), viewModel.getStepTitles(),
        viewModel.getInstruction(), viewModel.getMessage(), viewModel.getStatus());
    bench.showItems(viewModel.getItems());
    tray.showTray(viewModel.getTrayTemplates());
    nav.showLocks(viewModel.isPrevLocked(), viewModel.isNextLocked(), viewModel.getStepIndex(),
        viewModel.getStepCount());
  }
}
