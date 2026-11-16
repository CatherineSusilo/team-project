package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.AnswerCheckpointController;
import adapter.controller.ShowCheckpointController;
import adapter.presenter.AnswerCheckpointPresenter;
import adapter.presenter.ShowCheckpointPresenter;
import adapter.viewmodel.CheckpointViewModel;
import data.InMemoryLabDataAccessObject;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import usecase.answercheckpoint.AnswerCheckpointInteractor;
import usecase.showcheckpoint.ShowCheckpointInteractor;

class CheckpointViewTest {

  private static CheckpointView viewFor(CheckpointViewModel viewModel) {
    InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject();
    return new CheckpointView(viewModel,
        new ShowCheckpointController(
            new ShowCheckpointInteractor(dataAccess, new ShowCheckpointPresenter(viewModel))),
        new AnswerCheckpointController(
            new AnswerCheckpointInteractor(dataAccess, new AnswerCheckpointPresenter(viewModel))));
  }

  @Test
  void continueStaysLockedUntilTheAnswerIsCorrect() {
    CheckpointView view = viewFor(new CheckpointViewModel());
    boolean[] continued = {false};
    view.open(0, () -> continued[0] = true);

    AbstractButton next = button(view, "I understand, continue →");
    assertFalse(next.isEnabled(), "locked before answering");

    // Wrong answer (second choice): still locked.
    choices(view).get(1).setSelected(true);
    button(view, "Check Answer").doClick();
    assertFalse(next.isEnabled());

    // Right answer (first choice): unlocked, and Continue moves on.
    choices(view).get(0).setSelected(true);
    button(view, "Check Answer").doClick();
    assertTrue(next.isEnabled());
    next.doClick();
    assertTrue(continued[0]);
  }

  @Test
  void checkingWithNothingSelectedShowsAMessageAndStaysLocked() {
    CheckpointViewModel viewModel = new CheckpointViewModel();
    CheckpointView view = viewFor(viewModel);
    view.open(0, () -> { });
    button(view, "Check Answer").doClick();
    assertEquals("Pick an answer first.", viewModel.getMessage());
    assertFalse(button(view, "I understand, continue →").isEnabled());
  }

  @Test
  void openingAnotherStepReplacesTheQuestionAndRelocksContinue() {
    CheckpointView view = viewFor(new CheckpointViewModel());
    view.open(0, () -> { });
    choices(view).get(0).setSelected(true);
    button(view, "Check Answer").doClick();
    assertTrue(button(view, "I understand, continue →").isEnabled());

    view.open(1, () -> { });
    assertFalse(button(view, "I understand, continue →").isEnabled());
    assertTrue(button(view, "Check Answer").isEnabled());
  }

  private static List<AbstractButton> choices(Container container) {
    List<AbstractButton> found = new ArrayList<>();
    collect(container, found);
    found.removeIf(button -> button.getText().startsWith("Check")
        || button.getText().startsWith("I understand"));
    return found;
  }

  private static AbstractButton button(Container container, String text) {
    List<AbstractButton> found = new ArrayList<>();
    collect(container, found);
    return found.stream().filter(button -> button.getText().equals(text)).findFirst()
        .orElseThrow(() -> new AssertionError("No button " + text));
  }

  private static void collect(Container container, List<AbstractButton> found) {
    for (Component child : container.getComponents()) {
      if (child instanceof AbstractButton) {
        found.add((AbstractButton) child);
      }
      if (child instanceof Container) {
        collect((Container) child, found);
      }
    }
  }
}
