package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The student's lab notebook: a rich-text page beside the lab that stays open for the whole
 * module. Supports bold, italic, underline and strikethrough, with Google Docs shortcuts, and
 * chemical equations drawn from LaTeX.
 */
class NotebookPanel extends JPanel {
  private static final Color BACKGROUND = new Color(0xF5F5F5);
  private static final Color PLACEHOLDER = new Color(0xBDBDBD);
  private static final Color STYLE_ON = new Color(0xE0E0E0);
  private static final int SHORTCUT = System.getProperty("os.name", "").startsWith("Mac")
      ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

  // TODO: save the notes when the student exits the module, load them when it opens again and
  //  clear them on Reset (through RecordResultController, as Notebook.record("notes", html)).
  final JTextPane text = new JTextPane() {
    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      if (getDocument().getLength() == 0) {
        Insets insets = getInsets();
        graphics.setColor(PLACEHOLDER);
        graphics.setFont(getFont());
        graphics.drawString("Start writing here.", insets.left,
            insets.top + graphics.getFontMetrics().getAscent());
      }
    }
  };
  /** Style attribute -> its toolbar button, so the buttons can show the style at the caret. */
  private final Map<Object, JToggleButton> buttons = new LinkedHashMap<>();

  NotebookPanel() {
    super(new BorderLayout(0, 12));
    setBackground(BACKGROUND);
    setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

    JLabel heading = new JLabel("Your Lab Notebook");
    heading.setFont(Theme.bold(Theme.HEADER_SIZE));
    add(heading, BorderLayout.NORTH);

    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
    toolbar.setOpaque(false);
    Font font = Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE);
    addStyle(toolbar, StyleConstants.Bold, "B", "Bold", KeyEvent.VK_B, 0,
        Theme.bold(Theme.BODY_SIZE));
    addStyle(toolbar, StyleConstants.Italic, "I", "Italic", KeyEvent.VK_I, 0,
        font.deriveFont(Font.ITALIC));
    addStyle(toolbar, StyleConstants.Underline, "U", "Underline", KeyEvent.VK_U, 0,
        font.deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
    addStyle(toolbar, StyleConstants.StrikeThrough, "S", "Strikethrough", KeyEvent.VK_X,
        InputEvent.SHIFT_DOWN_MASK,
        font.deriveFont(Map.of(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON)));
    JButton equation = flat(new JButton("Equation"));
    equation.setToolTipText("Insert a chemical equation");
    equation.addActionListener(event -> openEquationPopup());
    toolbar.add(equation);
    toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PLACEHOLDER));

    text.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    // The editor updates its typing style on caret moves too, so read it after that.
    text.addCaretListener(event -> SwingUtilities.invokeLater(this::refreshButtons));
    JScrollPane scroll = new JScrollPane(text);
    scroll.setBorder(null);

    JPanel page = new JPanel(new BorderLayout());
    page.setBackground(Color.WHITE);
    page.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
    page.add(toolbar, BorderLayout.NORTH);
    page.add(scroll, BorderLayout.CENTER);
    add(page, BorderLayout.CENTER);
  }

  /** Adds a toolbar button and a keyboard shortcut that both toggle one text style. */
  private void addStyle(JPanel toolbar, Object style, String label, String name, int key,
      int extraModifiers, Font font) {
    AbstractAction toggle = new AbstractAction(label) {
      @Override
      public void actionPerformed(ActionEvent event) {
        toggle(style);
      }
    };
    JToggleButton button = flat(new JToggleButton(toggle));
    button.setFont(font);
    button.setToolTipText(name);
    button.getAccessibleContext().setAccessibleName(name);
    toolbar.add(button);
    buttons.put(style, button);

    text.getInputMap().put(KeyStroke.getKeyStroke(key, SHORTCUT | extraModifiers), name);
    text.getActionMap().put(name, toggle);
  }

  /**
   * Styles a toolbar button flat and compact, so all of them fit the column on every system.
   * Not focusable, so clicking it leaves the caret and selection in the text.
   */
  private static <T extends AbstractButton> T flat(T button) {
    button.setFocusable(false);
    button.setOpaque(true);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setBackground(Color.WHITE);
    button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return button;
  }

  /**
   * Turns a style on or off for the selection, or for what is typed next if nothing is
   * selected. Like Google Docs, it turns off only if the selection already starts with it.
   */
  void toggle(Object style) {
    SimpleAttributeSet change = new SimpleAttributeSet();
    change.addAttribute(style, !Boolean.TRUE.equals(currentStyle().getAttribute(style)));
    int start = text.getSelectionStart();
    int end = text.getSelectionEnd();
    if (start != end) {
      text.getStyledDocument().setCharacterAttributes(start, end - start, change, false);
    }
    text.getInputAttributes().addAttributes(change);
    refreshButtons();
  }

  /**
   * Puts a drawn equation on its own line where the caret is, replacing any selection. Own line
   * because Swing wraps the text around a picture wrongly, letting it run past the edge.
   *
   * @param latex the equation's LaTeX
   */
  void insertEquation(String latex) {
    SimpleAttributeSet equation = new SimpleAttributeSet();
    StyleConstants.setIcon(equation,
        ChemistryLatex.render(latex, EquationPanel.EQUATION_SIZE));
    int at = text.getSelectionStart();
    text.replaceSelection("");
    StyledDocument doc = text.getStyledDocument();
    try {
      if (at > 0 && !doc.getText(at - 1, 1).equals("\n")) {
        doc.insertString(at++, "\n", null);
      }
      // An icon takes the place of one character in the text.
      doc.insertString(at, " ", equation);
      if (at + 1 == doc.getLength() || !doc.getText(at + 1, 1).equals("\n")) {
        doc.insertString(at + 1, "\n", null);
      }
    } catch (BadLocationException e) {
      throw new IllegalStateException(e);
    }
    // Continue typing on the line after the equation.
    text.setCaretPosition(at + 2);
  }

  private void openEquationPopup() {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Insert Equation",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setContentPane(new EquationPanel(latex -> {
      insertEquation(latex);
      dialog.dispose();
    }, dialog::dispose));
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    // Blocks until the popup closes.
    Theme.showDimmed(dialog);
    text.requestFocusInWindow();
  }

  private AttributeSet currentStyle() {
    int start = text.getSelectionStart();
    return start == text.getSelectionEnd() ? text.getInputAttributes()
        : text.getStyledDocument().getCharacterElement(start).getAttributes();
  }

  private void refreshButtons() {
    AttributeSet current = currentStyle();
    buttons.forEach((style, button) -> {
      boolean on = Boolean.TRUE.equals(current.getAttribute(style));
      button.setSelected(on);
      button.setBackground(on ? STYLE_ON : Color.WHITE);
    });
  }
}
