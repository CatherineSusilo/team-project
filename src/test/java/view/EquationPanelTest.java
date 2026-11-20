package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;

class EquationPanelTest {

  @Test
  void insertIsLockedUntilSomethingIsTyped() {
    EquationPanel panel = new EquationPanel(latex -> { }, () -> { });
    assertFalse(panel.insert.isEnabled());
    assertEquals("Type an equation to see it here.", panel.preview.getText());
  }

  @Test
  void plainTextIsPreviewedAndInsertedAsLatex() {
    String[] inserted = {null};
    EquationPanel panel = new EquationPanel(latex -> inserted[0] = latex, () -> { });
    panel.input.setText("2H2 + O2 -> 2H2O");
    assertNotNull(panel.preview.getIcon());
    assertTrue(panel.insert.isEnabled());

    panel.insert.doClick();
    assertEquals(ChemistryLatex.toLatex("2H2 + O2 -> 2H2O"), inserted[0]);
  }

  @Test
  void symbolButtonsTypeAtTheCaret() {
    EquationPanel panel = new EquationPanel(latex -> { }, () -> { });
    panel.input.setText("CO2");
    panel.input.setCaretPosition(3);
    button(panel, "↑").doClick();
    assertEquals("CO2↑", panel.input.getText());

    panel.input.setText("Fe");
    panel.input.setCaretPosition(2);
    button(panel, "x²").doClick();
    assertEquals("Fe^{}", panel.input.getText());
    assertEquals(4, panel.input.getCaretPosition(), "caret goes inside the braces");
  }

  @Test
  void writingLatexStartsFromTheConvertedTextAndUsesItAsIs() {
    EquationPanel panel = new EquationPanel(latex -> { }, () -> { });
    panel.input.setText("H2O");
    panel.writeLatex.doClick();
    assertEquals("\\mathrm{H_{2}O}", panel.input.getText());
    assertEquals("\\mathrm{H_{2}O}", panel.latex());
  }

  @Test
  void latexThatCantBeReadShowsAMessageAndLocksInsert() {
    EquationPanel panel = new EquationPanel(latex -> { }, () -> { });
    panel.writeLatex.doClick();
    panel.input.setText("\\notacommand");
    assertNull(panel.preview.getIcon());
    assertFalse(panel.insert.isEnabled());
    assertTrue(panel.preview.getText().startsWith("Can't read this yet"));
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
