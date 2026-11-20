package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adapter.controller.SearchCompoundController;
import adapter.viewmodel.CompoundViewModel;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;

class CompoundSearchViewTest {
  private final CompoundViewModel viewModel = new CompoundViewModel();

  @Test
  void showsTheFoundCompound() throws Exception {
    CompoundSearchView view = new CompoundSearchView(viewModel,
        new SearchCompoundController(in -> { }));
    viewModel.setCompound("Diethyl Ether", "C4H10O", "74.12 g/mol", "-116.2 °C", "34.6 °C",
        "Danger", List.of("H224: Extremely flammable liquid and vapor"), null);
    SwingUtilities.invokeAndWait(viewModel::firePropertyChanged);

    List<String> texts = texts(view);
    assertTrue(texts.containsAll(List.of("Diethyl Ether", "C4H10O", "74.12 g/mol",
        "-116.2 °C", "34.6 °C", "Danger", "• H224: Extremely flammable liquid and vapor")));
    assertTrue(texts.contains("No structure picture"));
  }

  @Test
  void showsWhyTheSearchFailed() throws Exception {
    CompoundSearchView view = new CompoundSearchView(viewModel,
        new SearchCompoundController(in -> { }));
    viewModel.setError("No compound named \"x\" on PubChem.");
    SwingUtilities.invokeAndWait(viewModel::firePropertyChanged);

    List<String> texts = texts(view);
    assertTrue(texts.contains("No compound named \"x\" on PubChem."));
    assertFalse(texts.contains("Formula"));
  }

  @Test
  void searchRunsOffTheSwingThread() throws Exception {
    CountDownLatch searched = new CountDownLatch(1);
    boolean[] onSwingThread = {true};
    String[] name = {null};
    CompoundSearchView view = new CompoundSearchView(viewModel,
        new SearchCompoundController(in -> {
          onSwingThread[0] = SwingUtilities.isEventDispatchThread();
          name[0] = in.getName();
          searched.countDown();
        }));

    SwingUtilities.invokeAndWait(() -> button(view, "water").doClick());

    assertTrue(searched.await(5, TimeUnit.SECONDS));
    assertFalse(onSwingThread[0]);
    assertEquals("water", name[0]);
  }

  private static AbstractButton button(Container container, String text) {
    for (Component child : container.getComponents()) {
      if (child instanceof AbstractButton && text.equals(((AbstractButton) child).getText())) {
        return (AbstractButton) child;
      }
      if (child instanceof Container) {
        AbstractButton found = button((Container) child, text);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static List<String> texts(Container container) {
    List<String> texts = new ArrayList<>();
    for (Component child : container.getComponents()) {
      if (child instanceof JLabel) {
        texts.add(((JLabel) child).getText());
      } else if (child instanceof JTextComponent) {
        texts.add(((JTextComponent) child).getText());
      }
      if (child instanceof Container) {
        texts.addAll(texts((Container) child));
      }
    }
    return texts;
  }
}
