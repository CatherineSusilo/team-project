package view;

import adapter.viewmodel.ModuleState;
import adapter.viewmodel.PartState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Popup shown when a student clicks "See Instructions" on a module: the title on a green strip,
 * then the due date, overview and learning objectives on the left, "Your Progress" on the
 * right, and Reset and Start along the bottom.
 *
 * <p>The panel only displays the module; what each button does is passed in by whoever
 * creates it.
 */
public class ModulePanel extends JPanel {
  private static final int CIRCLE = 28;
  private static final int TEXT_WIDTH = 440;
  private static final int PROGRESS_WIDTH = 260;

  /**
   * Builds the popup for one module.
   *
   * @param module the module to describe
   * @param onStart called when the student clicks Start
   * @param onReset called after the student confirms they want to reset
   */
  public ModulePanel(ModuleState module, Runnable onStart, Runnable onReset) {
    super(new BorderLayout());
    setBackground(Color.WHITE);

    JLabel title = new JLabel(module.getTitle());
    title.setForeground(Theme.ON_GREEN);
    title.setFont(Theme.bold(title.getFont(), 20f));
    JPanel strip = new JPanel(new BorderLayout());
    strip.setBackground(Theme.GREEN);
    strip.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    strip.add(title);
    add(strip, BorderLayout.NORTH);

    JPanel body = new JPanel(new BorderLayout(32, 0));
    body.setOpaque(false);
    body.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
    body.add(createDetails(module), BorderLayout.CENTER);
    body.add(createProgress(module.getParts()), BorderLayout.EAST);
    add(body, BorderLayout.CENTER);

    JButton reset = Theme.filledButton("Reset", Theme.RED, Color.WHITE);
    reset.addActionListener(event -> {
      int choice = JOptionPane.showConfirmDialog(this,
          "Reset all your progress on " + module.getTitle() + "? This can't be undone.",
          "Reset module", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (choice == JOptionPane.YES_OPTION) {
        onReset.run();
      }
    });
    JButton start = Theme.filledButton("Start", Theme.GREEN, Theme.ON_GREEN);
    start.addActionListener(event -> onStart.run());
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
    buttons.setOpaque(false);
    buttons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
    buttons.add(reset);
    buttons.add(start);
    add(buttons, BorderLayout.SOUTH);
  }

  /** Due date, General Overview and Learning Objectives, top to bottom. */
  private static JPanel createDetails(ModuleState module) {
    JPanel details = new JPanel();
    details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
    details.setOpaque(false);
    JLabel due = new JLabel("Due: " + module.getDueDate());
    due.setFont(Theme.bold(due.getFont(), 14f));
    details.add(due);
    details.add(Box.createVerticalStrut(16));
    details.add(heading("General Overview"));
    details.add(paragraph(module.getOverview()));
    if (!module.getLearningObjectives().isEmpty()) {
      StringBuilder objectives = new StringBuilder();
      for (String objective : module.getLearningObjectives()) {
        objectives.append(objectives.length() == 0 ? "" : "\n").append("• ").append(objective);
      }
      details.add(Box.createVerticalStrut(16));
      details.add(heading("Learning Objectives"));
      details.add(paragraph(objectives.toString()));
    }
    return details;
  }

  /** "Your Progress", then one numbered row per part, e.g. "1  Pre-Procedure Quiz  80%". */
  private static JPanel createProgress(List<PartState> parts) {
    JPanel rows = new JPanel(new GridLayout(0, 1, 0, 12));
    rows.setOpaque(false);
    for (int i = 0; i < parts.size(); i++) {
      JLabel percent = new JLabel(parts.get(i).getProgress());
      percent.setFont(Theme.bold(percent.getFont(), 13f));
      JPanel row = new JPanel(new BorderLayout(12, 0));
      row.setOpaque(false);
      row.add(Theme.circle(String.valueOf(i + 1), CIRCLE, 13f), BorderLayout.WEST);
      row.add(new JLabel(parts.get(i).getTitle()), BorderLayout.CENTER);
      row.add(percent, BorderLayout.EAST);
      rows.add(row);
    }
    JPanel progress = new JPanel(new BorderLayout(0, 12));
    progress.setOpaque(false);
    progress.add(heading("Your Progress"), BorderLayout.NORTH);
    progress.add(rows, BorderLayout.CENTER);
    progress.setPreferredSize(new Dimension(PROGRESS_WIDTH, progress.getPreferredSize().height));
    // Keeps the rows at the top instead of spreading them down the popup.
    JPanel holder = new JPanel(new BorderLayout());
    holder.setOpaque(false);
    holder.add(progress, BorderLayout.NORTH);
    return holder;
  }

  private static JLabel heading(String text) {
    JLabel heading = new JLabel(text);
    heading.setFont(Theme.bold(heading.getFont(), 16f));
    heading.setAlignmentX(LEFT_ALIGNMENT);
    return heading;
  }

  /** Read-only wrapped text that looks like a label. */
  private static JTextArea paragraph(String text) {
    JTextArea area = new JTextArea(text);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setEditable(false);
    area.setOpaque(false);
    area.setFont(new JLabel().getFont());
    area.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    area.setAlignmentX(LEFT_ALIGNMENT);
    // Wrapping needs a width before Swing can work out how tall the text is.
    area.setSize(TEXT_WIDTH, Short.MAX_VALUE);
    return area;
  }
}
