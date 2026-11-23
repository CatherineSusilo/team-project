package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * The left panel: which step this is, what to do, and how the student is getting on.
 */
class InstructionPanel extends JPanel {

  private final JLabel counter = new JLabel();
  private final JLabel title = new JLabel();
  private final JTextArea instruction = new JTextArea();
  private final JTextArea banner = new JTextArea();

  /** Builds an empty panel. */
  InstructionPanel() {
    super(new BorderLayout(0, 12));
    setPreferredSize(new Dimension(300, 0));
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.DISABLED),
        BorderFactory.createEmptyBorder(16, 16, 16, 16)));

    counter.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL));
    counter.setForeground(Color.GRAY);
    title.setFont(Theme.bold(Theme.HEADER_SIZE));
    JPanel heading = new JPanel(new BorderLayout());
    heading.setOpaque(false);
    heading.add(counter, BorderLayout.NORTH);
    heading.add(title, BorderLayout.CENTER);
    add(heading, BorderLayout.NORTH);

    plain(instruction);
    instruction.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE));
    add(instruction, BorderLayout.CENTER);

    plain(banner);
    banner.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE));
    banner.setOpaque(true);
    banner.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    add(banner, BorderLayout.SOUTH);
  }

  private static void plain(JTextArea area) {
    area.setEditable(false);
    area.setFocusable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setOpaque(false);
  }

  /**
   * Shows a step.
   *
   * @param stepIndex the step's position, from 0
   * @param stepCount how many steps there are
   * @param stepTitle the step's name
   * @param text what the student is asked to do
   * @param message the hint or mistake to show under it, or "" for none
   * @param status INCOMPLETE, COMPLETE or MISTAKE
   */
  void showStep(int stepIndex, int stepCount, String stepTitle, String text, String message,
      String status) {
    counter.setText("Step " + (stepIndex + 1) + " of " + stepCount);
    title.setText(stepTitle);
    instruction.setText(text);
    banner.setText(message);
    banner.setVisible(!message.isEmpty());
    if ("MISTAKE".equals(status)) {
      banner.setBackground(new Color(0xFBE3E1));
      banner.setForeground(Theme.RED);
    } else if ("COMPLETE".equals(status)) {
      banner.setBackground(new Color(0xE0F2E9));
      banner.setForeground(Theme.GREEN);
    } else {
      banner.setBackground(new Color(0xF0F0F0));
      banner.setForeground(Color.DARK_GRAY);
    }
  }

  String getInstructionText() {
    return instruction.getText();
  }

  String getBannerText() {
    return banner.getText();
  }
}
