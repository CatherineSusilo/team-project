package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The dashboard's View Lab Notebook popup: one row per module's notebook. Clicking a row opens
 * that notebook to read and edit, and "← All notebooks" goes back to the list.
 */
class NotebookListPanel extends JPanel {
  private static final Color ROW = new Color(0xF5F5F5);
  private final List<String> modules;

  /**
   * Builds the popup, showing the list.
   *
   * @param modules the titles of the modules whose notebooks to list
   */
  NotebookListPanel(List<String> modules) {
    super(new BorderLayout(0, 16));
    this.modules = List.copyOf(modules);
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
    setPreferredSize(new Dimension(480, 600));
    showList();
  }

  private void showList() {
    JLabel heading = new JLabel("Your Lab Notebooks");
    heading.setFont(Theme.bold(Theme.HEADER_SIZE));

    JPanel rows = new JPanel(new GridLayout(0, 1, 0, 8));
    rows.setOpaque(false);
    for (String module : modules) {
      JButton row = Theme.filledButton(module, ROW, Color.BLACK);
      row.setHorizontalAlignment(SwingConstants.LEFT);
      row.addActionListener(event -> open(module));
      rows.add(row);
    }
    if (modules.isEmpty()) {
      rows.add(new JLabel("No notebooks yet. Your notebook starts when you begin the lab."));
    }
    // NORTH keeps the rows at their natural height instead of stretching to fill the popup.
    JPanel list = new JPanel(new BorderLayout());
    list.setOpaque(false);
    list.add(rows, BorderLayout.NORTH);
    show(heading, list);
  }

  private void open(String module) {
    JButton back = Theme.linkButton("← All notebooks", Theme.GREEN);
    back.addActionListener(event -> showList());
    // Line the link up with the notebook's left edge, with a gap before the title.
    back.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
    JLabel title = new JLabel(module);
    title.setFont(Theme.bold(Theme.HEADER_SIZE));
    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    top.setOpaque(false);
    top.add(back);
    top.add(title);
    // TODO: load this module's saved notebook here, and save the edits when it closes.
    show(top, new NotebookPanel());
  }

  private void show(Component top, Component body) {
    removeAll();
    add(top, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
    revalidate();
    repaint();
  }
}
