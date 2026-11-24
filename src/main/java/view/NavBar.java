package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The bottom of the Lab Manual: Exit Module on the left and Continue on the right, with a small
 * link to go back a step. Continue is locked until the step is complete.
 */
class NavBar extends JPanel {

  private static final Color LINK_BLUE = new Color(0x1A47D6);
  private static final Color LOCKED_TEXT = new Color(0x9AA5A4);
  private static final Color LOCKED_FILL = new Color(0xE7ECEC);

  private final JButton prev = Theme.linkButton("← Previous step", Color.DARK_GRAY);
  private final JButton exit = Theme.linkButton("Exit Module", LINK_BLUE);
  private final JButton next = Theme.filledButton("Continue", Theme.GREEN, Theme.ON_GREEN);

  /**
   * Builds the bar.
   *
   * @param onPrev runs when Previous step is clicked
   * @param onNext runs when Continue is clicked
   * @param onExit runs when Exit Module is clicked
   */
  NavBar(Runnable onPrev, Runnable onNext, Runnable onExit) {
    super(new BorderLayout(0, 4));
    setOpaque(false);
    setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));
    prev.addActionListener(event -> onPrev.run());
    next.addActionListener(event -> onNext.run());
    exit.addActionListener(event -> onExit.run());

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    top.setOpaque(false);
    top.add(prev);
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setOpaque(false);
    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    left.setOpaque(false);
    left.add(exit);
    bottom.add(left, BorderLayout.WEST);
    bottom.add(next, BorderLayout.EAST);
    add(top, BorderLayout.NORTH);
    add(bottom, BorderLayout.CENTER);
    showLocks(true, true, 0, 1);
  }

  /**
   * Enables or locks the buttons and says where Continue leads.
   *
   * @param prevLocked true if there is no step to go back to
   * @param nextLocked true if the step isn't complete yet
   * @param stepIndex the current step, from 0
   * @param stepCount how many steps there are
   */
  void showLocks(boolean prevLocked, boolean nextLocked, int stepIndex, int stepCount) {
    prev.setEnabled(!prevLocked);
    prev.setVisible(!prevLocked);
    next.setEnabled(!nextLocked);
    next.setText(stepIndex + 1 < stepCount ? "Continue to Step " + (stepIndex + 2)
        : "Next Part →");
    next.setBackground(nextLocked ? LOCKED_FILL : Theme.GREEN);
    next.setForeground(nextLocked ? LOCKED_TEXT : Theme.ON_GREEN);
  }

  JButton getPrevButton() {
    return prev;
  }

  JButton getExitButton() {
    return exit;
  }

  JButton getNextButton() {
    return next;
  }
}
