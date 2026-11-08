package view;

import entity.LabModule;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Detail panel shown when a student clicks "See Instructions" on a module: title, due date,
 * overview, and start/continue/reset buttons. Continue only shows once the module is started.
 *
 * <p>The panel only displays the module; what each button does (opening the pre-lab, loading
 * or clearing saved progress) is passed in by whoever creates it.
 */
public class ModulePanel extends JPanel {

  /**
   * Builds the detail panel for one module.
   *
   * @param module the module to describe
   * @param onStart called when the student clicks Start
   * @param onContinue called when the student clicks Continue
   * @param onReset called after the student confirms they want to reset
   */
  public ModulePanel(LabModule module, Runnable onStart, Runnable onContinue,
      Runnable onReset) {
    super(new BorderLayout(0, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    JLabel title = new JLabel(module.getTitle());
    title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
    JPanel header = new JPanel(new GridLayout(0, 1));
    header.add(title);
    header.add(new JLabel("Due: " + module.getDueDate()));
    add(header, BorderLayout.NORTH);

    JTextArea overview = new JTextArea(module.getOverview(), 14, 50);
    overview.setLineWrap(true);
    overview.setWrapStyleWord(true);
    overview.setEditable(false);
    add(new JScrollPane(overview), BorderLayout.CENTER);

    JButton start = new JButton("Start");
    start.addActionListener(event -> onStart.run());
    JButton reset = new JButton("Reset");
    reset.addActionListener(event -> {
      int choice = JOptionPane.showConfirmDialog(this,
          "Reset all your progress on " + module.getTitle() + "? This can't be undone.",
          "Reset module", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (choice == JOptionPane.YES_OPTION) {
        onReset.run();
      }
    });
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(reset);
    if (module.getLastEdited().isPresent()) {
      JButton continueButton = new JButton("Continue");
      continueButton.addActionListener(event -> onContinue.run());
      buttons.add(continueButton);
    }
    buttons.add(start);
    add(buttons, BorderLayout.SOUTH);
  }
}
