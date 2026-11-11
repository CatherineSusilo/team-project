package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Popup shown once the pre-procedure quiz is submitted: a green check, "Good Job!", and
 * Exit Module, Save Progress and Proceed to Experiment.
 *
 * <p>The answers are already saved by then, so the panel only says what to do next; what each
 * button does is passed in by whoever creates it.
 */
class QuizCompletePanel extends JPanel {

  /**
   * Builds the popup.
   *
   * @param onExit called when the student clicks Exit Module
   * @param onSaveProgress called when the student clicks Save Progress
   * @param onProceed called when the student clicks Proceed to Experiment
   */
  QuizCompletePanel(Runnable onExit, Runnable onSaveProgress, Runnable onProceed) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createEmptyBorder(32, 48, 24, 48));

    centered(Theme.circle("✓", 72, 36f));
    add(Box.createVerticalStrut(16));
    JLabel goodJob = new JLabel("Good Job!");
    goodJob.setFont(Theme.bold(goodJob.getFont(), 24f));
    centered(goodJob);
    add(Box.createVerticalStrut(8));
    centered(new JLabel("You completed the Pre-Procedure Quiz!"));
    add(Box.createVerticalStrut(24));

    JButton exit = Theme.linkButton("Exit Module", Theme.GREEN);
    exit.addActionListener(event -> onExit.run());
    JButton save = Theme.filledButton("Save Progress", Theme.DISABLED, Color.BLACK);
    save.addActionListener(event -> onSaveProgress.run());
    JButton proceed = Theme.filledButton("Proceed to Experiment", Theme.GREEN, Theme.ON_GREEN);
    proceed.addActionListener(event -> onProceed.run());
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    buttons.setOpaque(false);
    buttons.add(exit);
    buttons.add(save);
    buttons.add(proceed);
    centered(buttons);
  }

  private void centered(JComponent component) {
    component.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(component);
  }
}
