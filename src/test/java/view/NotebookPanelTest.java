package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import org.junit.jupiter.api.Test;

class NotebookPanelTest {

  @Test
  void eachButtonStylesTheSelectionAndClickingAgainRemovesIt() {
    for (String label : new String[] {"B", "I", "U", "S"}) {
      NotebookPanel notebook = new NotebookPanel();
      notebook.text.setText("melting point 121 C");
      notebook.text.select(0, 7);

      button(notebook, label).doClick();
      assertTrue(isOn(styleAt(notebook, 0), label), label + " on");
      assertFalse(isOn(styleAt(notebook, 8), label), label + " leaves the rest alone");

      button(notebook, label).doClick();
      assertFalse(isOn(styleAt(notebook, 0), label), label + " off");
    }
  }

  @Test
  void withNothingSelectedTheStyleAppliesToWhatIsTypedNext() {
    NotebookPanel notebook = new NotebookPanel();
    button(notebook, "B").doClick();
    notebook.text.replaceSelection("mass");
    assertTrue(isOn(styleAt(notebook, 0), "B"));
    assertTrue(button(notebook, "B").isSelected());
  }

  @Test
  void shortcutsToggleStyles() {
    NotebookPanel notebook = new NotebookPanel();
    notebook.text.setText("yield");
    notebook.text.selectAll();
    int shortcut = System.getProperty("os.name", "").startsWith("Mac")
        ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
    press(notebook, KeyStroke.getKeyStroke(KeyEvent.VK_B, shortcut));
    press(notebook, KeyStroke.getKeyStroke(KeyEvent.VK_X,
        shortcut | InputEvent.SHIFT_DOWN_MASK));
    assertTrue(isOn(styleAt(notebook, 0), "B"));
    assertTrue(isOn(styleAt(notebook, 0), "S"));
  }

  @Test
  void insertedEquationReplacesTheSelectionOnItsOwnLineAndKeepsItsLatex() throws Exception {
    NotebookPanel notebook = new NotebookPanel();
    notebook.text.setText("a xx b");
    notebook.text.select(2, 4);

    notebook.insertEquation("\\mathrm{H_{2}O}");

    // "a " + newline + equation + newline + " b"
    assertEquals("a \n \n b", notebook.text.getDocument().getText(0, 7));
    Object icon = StyleConstants.getIcon(styleAt(notebook, 3));
    assertTrue(icon instanceof ChemistryLatex.EquationIcon);
    assertEquals("\\mathrm{H_{2}O}", ((ChemistryLatex.EquationIcon) icon).getLatex());
    assertEquals(5, notebook.text.getCaretPosition(), "caret goes to the line after");
  }

  @Test
  void equationInAnEmptyNotebookAddsNoBlankLineAbove() throws Exception {
    NotebookPanel notebook = new NotebookPanel();
    notebook.insertEquation("x");
    assertEquals(" \n", notebook.text.getDocument().getText(0, 2));
    assertEquals(2, notebook.text.getCaretPosition());
  }

  private static void press(NotebookPanel notebook, KeyStroke key) {
    Object name = notebook.text.getInputMap().get(key);
    notebook.text.getActionMap().get(name)
        .actionPerformed(new ActionEvent(notebook.text, ActionEvent.ACTION_PERFORMED, ""));
  }

  private static AttributeSet styleAt(NotebookPanel notebook, int offset) {
    return notebook.text.getStyledDocument().getCharacterElement(offset).getAttributes();
  }

  private static boolean isOn(AttributeSet style, String label) {
    Object key = label.equals("B") ? StyleConstants.Bold
        : label.equals("I") ? StyleConstants.Italic
        : label.equals("U") ? StyleConstants.Underline
        : StyleConstants.StrikeThrough;
    return Boolean.TRUE.equals(style.getAttribute(key));
  }

  private static AbstractButton button(Container root, String text) {
    for (Component child : root.getComponents()) {
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
}
