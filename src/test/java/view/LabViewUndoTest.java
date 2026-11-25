package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import usecase.LabStateMapper;
import usecase.moveitem.MoveItemInteractor;
import usecase.navigatestep.NavigateStepInteractor;
import usecase.spawnitem.SpawnItemInteractor;

class LabViewUndoTest {

  private static JButton findButton(Container root, String text) {
    for (Component child : root.getComponents()) {
      if (child instanceof JButton && text.equals(((JButton) child).getText())) {
        return (JButton) child;
      }
      if (child instanceof Container) {
        JButton found = findButton((Container) child, text);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  @Test
  void clickingUndoRunsTheUndoAction() throws Exception {
    InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject();
    LabViewModel viewModel = new LabViewModel();
    viewModel.setState(LabStateMapper.from(dataAccess.getLab()));
    AtomicInteger undone = new AtomicInteger();

    SwingUtilities.invokeAndWait(() -> {
      LabView view = new LabView(viewModel,
          new NavigateStepController(
              new NavigateStepInteractor(dataAccess, new NavigateStepPresenter(viewModel))),
          new SpawnItemController(new SpawnItemInteractor(dataAccess,
              new SpawnItemPresenter(viewModel), new ItemFactory())),
          new MoveItemController(
              new MoveItemInteractor(dataAccess, new MoveItemPresenter(viewModel))));
      view.setOnUndo(undone::incrementAndGet);
      JButton undo = findButton(view, "Undo");
      assertNotNull(undo);
      undo.doClick();
    });

    assertEquals(1, undone.get());
  }
}
