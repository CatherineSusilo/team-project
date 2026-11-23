package view;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The Prev and Next buttons along the bottom of the lab. Next is locked until the step is
 * complete.
 */
class NavBar extends JPanel {

  private final JButton prev = Theme.filledButton("← Prev", Theme.DISABLED, Color.BLACK);
  private final JButton next = Theme.filledButton("Next →", Theme.GREEN, Theme.ON_GREEN);

  /**
   * Builds the bar.
   *
   * @param onPrev runs when Prev is clicked
   * @param onNext runs when Next is clicked
   */
  NavBar(Runnable onPrev, Runnable onNext) {
    super(new FlowLayout(FlowLayout.RIGHT, 12, 8));
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.DISABLED));
    prev.addActionListener(event -> onPrev.run());
    next.addActionListener(event -> onNext.run());
    add(prev);
    add(next);
    showLocks(true, true);
  }

  /**
   * Enables or locks the buttons.
   *
   * @param prevLocked true if there is no step to go back to
   * @param nextLocked true if the step isn't complete yet
   */
  void showLocks(boolean prevLocked, boolean nextLocked) {
    prev.setEnabled(!prevLocked);
    next.setEnabled(!nextLocked);
    next.setBackground(nextLocked ? Theme.DISABLED : Theme.GREEN);
    next.setForeground(nextLocked ? Color.GRAY : Theme.ON_GREEN);
  }

  JButton getPrevButton() {
    return prev;
  }

  JButton getNextButton() {
    return next;
  }
}
