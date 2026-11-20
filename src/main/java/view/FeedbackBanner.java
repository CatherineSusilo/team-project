package view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The strip that tells the student about a mistake as soon as they make it. It is red, shows
 * what went wrong, and has an Undo button. It stays hidden while there is nothing to say.
 */
public class FeedbackBanner extends JPanel {
  private static final Color BACKGROUND = new Color(0xFDECEA);

  private final JLabel message = new JLabel("");
  private Runnable onUndo = () -> { };

  /**
   * Creates a hidden banner.
   */
  public FeedbackBanner() {
    super(new BorderLayout(12, 0));
    setBackground(BACKGROUND);
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.RED),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)));

    message.setForeground(Theme.RED);
    message.setFont(Theme.bold(Theme.BODY_SIZE));
    add(message, BorderLayout.CENTER);

    JButton undo = Theme.filledButton("Undo", Theme.RED, Theme.ON_GREEN);
    undo.addActionListener(event -> onUndo.run());
    add(undo, BorderLayout.EAST);

    setVisible(false);
  }

  /**
   * Sets what happens when the student clicks Undo. Until it is set, clicking does nothing.
   *
   * @param onUndo the action to run
   */
  public void setOnUndo(Runnable onUndo) {
    this.onUndo = onUndo;
  }

  /**
   * Shows a mistake, replacing any earlier message.
   *
   * @param text what went wrong and how to fix it
   */
  public void showMistake(String text) {
    message.setText(text);
    setVisible(true);
  }

  /**
   * Hides the banner and forgets its message.
   */
  public void clear() {
    message.setText("");
    setVisible(false);
  }
}
