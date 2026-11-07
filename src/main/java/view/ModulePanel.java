package view;

import entity.LabModule;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Detail panel shown when a student clicks "See Instructions" on a module: title, due date,
 * overview, and start/continue/reset buttons. Continue only shows once the module is started.
 */
public class ModulePanel extends JPanel {

  /**
   * Builds the detail panel for one module.
   *
   * @param module the module to describe
   */
  public ModulePanel(LabModule module) {
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

    // TODO: open the pre-lab when Start is clicked.
    JButton start = new JButton("Start");
    // TODO: clear the student's saved progress for this module when Reset is clicked.
    JButton reset = new JButton("Reset");
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(reset);
    if (module.getLastEdited().isPresent()) {
      // TODO: reopen the pre-lab where the student left off.
      buttons.add(new JButton("Continue"));
    }
    buttons.add(start);
    add(buttons, BorderLayout.SOUTH);
  }
}
