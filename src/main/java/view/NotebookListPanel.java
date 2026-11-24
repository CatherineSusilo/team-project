package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The dashboard's View Lab Notebook popup: one row per module's notebook. Clicking a row opens
 * that notebook to read and edit, and "← All notebooks" goes back to the list. Leaving a
 * notebook saves it if it changed; if that fails, the notebook stays open.
 */
class NotebookListPanel extends JPanel {
  private static final Color ROW = new Color(0xF5F5F5);
  /** Module title -> its notes as HTML, kept current as notebooks are saved. */
  private final Map<String, String> notebooks;
  private final BiPredicate<String, String> onSave;
  private String openModule;
  private NotebookPanel openNotebook;

  /**
   * Builds the popup, showing the list.
   *
   * @param notebooks each module's title and its saved notes as HTML, in display order
   * @param onSave saves a module's notes, given its title and the notes as HTML, and says
   *     whether that worked
   */
  NotebookListPanel(Map<String, String> notebooks, BiPredicate<String, String> onSave) {
    super(new BorderLayout(0, 16));
    this.notebooks = new LinkedHashMap<>(notebooks);
    this.onSave = onSave;
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
    for (String module : notebooks.keySet()) {
      JButton row = Theme.filledButton(module, ROW, Color.BLACK);
      row.setHorizontalAlignment(SwingConstants.LEFT);
      row.addActionListener(event -> open(module));
      rows.add(row);
    }
    if (notebooks.isEmpty()) {
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
    back.addActionListener(event -> {
      if (leaveNotebook()) {
        showList();
      }
    });
    // Line the link up with the notebook's left edge, with a gap before the title.
    back.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
    JLabel title = new JLabel(module);
    title.setFont(Theme.bold(Theme.HEADER_SIZE));
    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    top.setOpaque(false);
    top.add(back);
    top.add(title);
    openModule = module;
    openNotebook = new NotebookPanel();
    openNotebook.setHtml(notebooks.get(module));
    show(top, openNotebook);
  }

  /**
   * Saves the open notebook if it changed. Call it when the popup closes, too.
   *
   * @return false if the save failed, so the notebook is still open and unsaved
   */
  boolean leaveNotebook() {
    if (openNotebook == null) {
      return true;
    }
    String html = openNotebook.getHtml();
    if (!html.equals(notebooks.get(openModule))) {
      if (!onSave.test(openModule, html)) {
        return false;
      }
      notebooks.put(openModule, html);
    }
    openNotebook = null;
    return true;
  }

  private void show(Component top, Component body) {
    removeAll();
    add(top, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
    revalidate();
    repaint();
  }
}
