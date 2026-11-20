package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * The Insert Equation popup. Students type chemistry as plain text (or click symbols) and see
 * it drawn as they type. Students who know LaTeX can switch to writing it themselves.
 */
class EquationPanel extends JPanel {
  /** Text size of drawn equations, a little larger than the notebook text to match it. */
  static final float EQUATION_SIZE = 16f;
  private static final Color HINT = new Color(0x757575);
  /**
   * {button label, text it inserts, how far back from the end the caret goes}. Arrows insert
   * what you'd type for them, so students pick up the shortcut.
   */
  private static final String[][] SYMBOLS = {
      {"→", " -> ", "0"}, {"⇌", " <=> ", "0"}, {"↑", "↑", "0"}, {"↓", "↓", "0"},
      {"Δ", "Δ", "0"}, {"°", "°", "0"}, {"·", "·", "0"}, {"x₂", "_{}", "1"}, {"x²", "^{}", "1"}};

  final JTextField input = new JTextField(28);
  final JCheckBox writeLatex = new JCheckBox("I know LaTeX, let me write it myself");
  final JLabel preview = new JLabel("", SwingConstants.CENTER);
  final JButton insert = Theme.filledButton("Insert", Theme.GREEN, Theme.ON_GREEN);

  /**
   * Builds the popup.
   *
   * @param onInsert gets the LaTeX for the equation when the student clicks Insert
   * @param onCancel called when the student clicks Cancel
   */
  EquationPanel(Consumer<String> onInsert, Runnable onCancel) {
    super(new BorderLayout(0, 16));
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

    JLabel heading = new JLabel("Insert Chemical Equation");
    heading.setFont(Theme.bold(Theme.HEADER_SIZE));
    add(heading, BorderLayout.NORTH);

    JPanel symbols = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    symbols.setOpaque(false);
    for (String[] symbol : SYMBOLS) {
      JButton button = new JButton(symbol[0]);
      button.setFocusable(false);
      button.setToolTipText("Insert " + symbol[1].trim());
      if (button.getFont().canDisplayUpTo(symbol[0]) != -1) {
        // IBM Plex has no ⇌, so use Java's built-in font, which falls back to any system font.
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, button.getFont().getSize()));
      }
      button.addActionListener(event -> type(symbol[1], Integer.parseInt(symbol[2])));
      symbols.add(button);
    }

    preview.setPreferredSize(new Dimension(440, 80));
    preview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
    preview.setBorder(BorderFactory.createLineBorder(Theme.DISABLED));

    JPanel body = new JPanel();
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setOpaque(false);
    body.add(left(new JLabel("Type it the way you'd write it:")));
    body.add(left(input));
    body.add(left(hint("Examples:  2H2 + O2 -> 2H2O     Na+ + Cl-     Cu^2+(aq)     A <=> B")));
    body.add(left(hint("A charge with a number needs ^, like Fe^2+ or SO4^2-")));
    body.add(Box.createVerticalStrut(8));
    body.add(left(symbols));
    body.add(Box.createVerticalStrut(12));
    body.add(left(new JLabel("Preview:")));
    body.add(left(preview));
    body.add(left(writeLatex));
    add(body, BorderLayout.CENTER);

    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(event -> onCancel.run());
    insert.addActionListener(event -> onInsert.accept(latex()));
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    buttons.setOpaque(false);
    buttons.add(cancel);
    buttons.add(insert);
    add(buttons, BorderLayout.SOUTH);

    input.addActionListener(event -> insert.doClick());
    input.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent event) {
        refresh();
      }

      @Override
      public void removeUpdate(DocumentEvent event) {
        refresh();
      }

      @Override
      public void changedUpdate(DocumentEvent event) {
        refresh();
      }
    });
    // Switching to LaTeX starts from the converted text, so the student can tweak it.
    writeLatex.addActionListener(event -> {
      if (writeLatex.isSelected() && !input.getText().isBlank()) {
        input.setText(ChemistryLatex.toLatex(input.getText()));
      }
      refresh();
    });
    writeLatex.setOpaque(false);
    refresh();
  }

  /** The LaTeX for what is typed: converted from plain text unless the student writes LaTeX. */
  String latex() {
    return writeLatex.isSelected() ? input.getText() : ChemistryLatex.toLatex(input.getText());
  }

  private void refresh() {
    preview.setIcon(null);
    preview.setForeground(HINT);
    insert.setEnabled(false);
    if (input.getText().isBlank()) {
      preview.setText("Type an equation to see it here.");
      return;
    }
    try {
      preview.setIcon(ChemistryLatex.render(latex(), EQUATION_SIZE));
      preview.setText("");
      insert.setEnabled(true);
    } catch (RuntimeException e) {
      preview.setForeground(Theme.RED);
      preview.setText("Can't read this yet. Check the brackets and symbols.");
    }
  }

  private void type(String text, int caretBack) {
    int start = input.getSelectionStart();
    input.replaceSelection(text);
    input.setCaretPosition(start + text.length() - caretBack);
    input.requestFocusInWindow();
  }

  private static JLabel hint(String text) {
    JLabel hint = new JLabel(text);
    hint.setForeground(HINT);
    hint.setFont(hint.getFont().deriveFont(Theme.BODY_SIZE_SMALL));
    return hint;
  }

  private static JComponent left(JComponent component) {
    component.setAlignmentX(LEFT_ALIGNMENT);
    if (component instanceof JTextComponent) {
      component.setMaximumSize(new Dimension(Integer.MAX_VALUE,
          component.getPreferredSize().height));
    }
    return component;
  }
}
