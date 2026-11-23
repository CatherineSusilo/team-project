package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.MoveItemController;
import adapter.controller.NavigateStepController;
import adapter.controller.SpawnItemController;
import adapter.presenter.MoveItemPresenter;
import adapter.presenter.NavigateStepPresenter;
import adapter.presenter.SpawnItemPresenter;
import adapter.viewmodel.LabViewModel;
import data.InMemoryLabDataAccessObject;
import entity.ItemFactory;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.moveitem.MoveItemInteractor;
import usecase.navigatestep.NavigateStepInteractor;
import usecase.spawnitem.SpawnItemInteractor;

class LabViewTest {

  private final InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject();
  private final LabViewModel viewModel = new LabViewModel();
  private final NavigateStepController navigate = new NavigateStepController(
      new NavigateStepInteractor(dataAccess, new NavigateStepPresenter(viewModel)));
  private final SpawnItemController spawn = new SpawnItemController(
      new SpawnItemInteractor(dataAccess, new SpawnItemPresenter(viewModel), new ItemFactory()));
  private final MoveItemController move = new MoveItemController(
      new MoveItemInteractor(dataAccess, new MoveItemPresenter(viewModel)));

  private LabView build() {
    viewModel.setState(LabStateMapper.from(dataAccess.getLab()));
    return new LabView(viewModel, navigate, spawn, move);
  }

  private static <T extends Component> T find(Container root, Class<T> type) {
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) {
        return type.cast(child);
      }
      if (child instanceof Container) {
        T found = find((Container) child, type);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static void onEdt(Runnable action) throws Exception {
    SwingUtilities.invokeAndWait(action);
  }

  @Test
  void showsTheFirstStepWithNextAvailableBecauseItNeedsNothing() throws Exception {
    onEdt(() -> {
      LabView view = build();
      InstructionPanel panel = find(view, InstructionPanel.class);
      NavBar nav = find(view, NavBar.class);

      assertTrue(panel.getInstructionText().contains("batch letter"));
      assertTrue(nav.getNextButton().isEnabled());
      assertFalse(nav.getPrevButton().isEnabled());
    });
  }

  @Test
  void nextLocksAgainOnTheNextStepAndTheTrayShowsItsEquipment() throws Exception {
    onEdt(() -> {
      LabView view = build();
      NavBar nav = find(view, NavBar.class);
      nav.getNextButton().doClick();

      assertFalse(nav.getNextButton().isEnabled());
      assertTrue(nav.getPrevButton().isEnabled());
      assertEquals(List.of("conical_tube", "test_tube"), find(view, EquipmentPanel.class)
          .getShown());
    });
  }

  @Test
  void droppingTrayItemsOnTheBenchCompletesTheStep() throws Exception {
    onEdt(() -> {
      LabView view = build();
      NavBar nav = find(view, NavBar.class);
      nav.getNextButton().doClick();
      spawn.execute("conical_tube", 200, 200);
      spawn.execute("test_tube", 320, 200);

      assertTrue(nav.getNextButton().isEnabled());
      BenchPanel bench = find(view, BenchPanel.class);
      assertEquals("conical_tube-1", bench.itemAt(new Point(200, 200)));
    });
  }
}
