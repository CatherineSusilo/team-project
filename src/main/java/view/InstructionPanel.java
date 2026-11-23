package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * The Lab Manual: every step in a column. Steps already done show a check mark, the current step
 * is a yellow card with its instructions and any hint or mistake, and later steps are greyed out.
 */
class InstructionPanel extends JPanel {

  private static final Color CARD_FILL = new Color(0xFFF3C7);
  private static final Color ROW_FILL = new Color(0xEEF3F3);
  private static final Color DONE_GREEN = new Color(0x2E9E5B);
  private static final Color QUIET_TEXT = new Color(0x7A8584);
  private static final int ROW_HEIGHT = 44;
  private static final int GAP = 8;
  private static final int TEXT_WIDTH = 236;

  private final JPanel rows = new JPanel();
  private final JScrollPane scroll = new JScrollPane(rows,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
  private final JTextArea instruction = new JTextArea();
  private final JTextArea banner = new JTextArea();

  /** Builds an empty manual. */
  InstructionPanel() {
    super(new BorderLayout(0, 8));
    setBackground(new Color(0xFBFBFB));
    setBorder(BorderFactory.createEmptyBorder(14, 12, 0, 12));

    JLabel heading = new JLabel("Lab Manual");
    heading.setFont(Theme.bold(Theme.HEADER_SIZE));
    add(heading, BorderLayout.NORTH);

    rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
    rows.setOpaque(false);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    add(scroll, BorderLayout.CENTER);

    plain(instruction);
    plain(banner);
    banner.setOpaque(true);
    banner.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
  }

  private static void plain(JTextArea area) {
    area.setEditable(false);
    area.setFocusable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setOpaque(false);
    area.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL + 1f));
    area.setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  /**
   * Shows the manual.
   *
   * @param stepIndex the current step's position, from 0
   * @param stepTitles every step's name, in order
   * @param text what the student is asked to do now
   * @param message the hint or mistake to show under it, or "" for none
   * @param status INCOMPLETE, COMPLETE or MISTAKE
   */
  void showStep(int stepIndex, List<String> stepTitles, String text, String message,
      String status) {
    instruction.setText(text);
    banner.setText(message);
    banner.setVisible(!message.isEmpty());
    if ("MISTAKE".equals(status)) {
      banner.setBackground(new Color(0xFBE3E1));
      banner.setForeground(Theme.RED);
    } else if ("COMPLETE".equals(status)) {
      banner.setBackground(new Color(0xE0F2E9));
      banner.setForeground(DONE_GREEN);
    } else {
      banner.setBackground(new Color(0xFFE9A0));
      banner.setForeground(Color.DARK_GRAY);
    }

    rows.removeAll();
    JPanel current = null;
    for (int i = 0; i < stepTitles.size(); i++) {
      JPanel row;
      if (i < stepIndex) {
        row = doneRow(stepTitles.get(i));
      } else if (i == stepIndex) {
        current = currentCard(i);
        row = current;
      } else {
        row = laterRow(i, stepTitles.get(i));
      }
      rows.add(row);
      rows.add(Box.createVerticalStrut(GAP));
    }
    rows.revalidate();
    rows.repaint();
    if (current != null) {
      JPanel target = current;
      SwingUtilities.invokeLater(() -> rows.scrollRectToVisible(target.getBounds()));
    }
  }

  private JPanel currentCard(int index) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(CARD_FILL);
    card.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(Theme.YELLOW, 2, true),
        BorderFactory.createEmptyBorder(10, 12, 12, 12)));
    card.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel title = new JLabel("Step " + (index + 1));
    title.setFont(Theme.bold(Theme.HEADER_SIZE));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);
    card.add(title);
    card.add(Box.createVerticalStrut(6));
    card.add(fitted(instruction, TEXT_WIDTH));
    if (banner.isVisible()) {
      card.add(Box.createVerticalStrut(10));
      card.add(fitted(banner, TEXT_WIDTH));
    }
    return card;
  }

  /** Wrapped text only knows how tall it is once it has a width, so give it one. */
  private static JTextArea fitted(JTextArea area, int width) {
    area.setPreferredSize(null);
    area.setSize(width, Short.MAX_VALUE);
    area.setPreferredSize(new Dimension(width, area.getPreferredSize().height));
    area.setMaximumSize(new Dimension(Short.MAX_VALUE, area.getPreferredSize().height));
    return area;
  }

  private static JPanel laterRow(int index, String title) {
    JPanel row = new JPanel(new BorderLayout(10, 0));
    styleRow(row);
    JLabel number = new JLabel("Step " + (index + 1));
    number.setFont(Theme.bold(Theme.HEADER_SIZE));
    JLabel what = new JLabel(title);
    what.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL));
    what.setForeground(QUIET_TEXT);
    row.add(number, BorderLayout.WEST);
    row.add(what, BorderLayout.CENTER);
    return row;
  }

  private static JPanel doneRow(String title) {
    JPanel row = new JPanel(new BorderLayout(10, 0));
    styleRow(row);
    row.add(new CheckMark(), BorderLayout.WEST);
    JLabel what = new JLabel(title);
    what.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL));
    what.setForeground(QUIET_TEXT);
    row.add(what, BorderLayout.CENTER);
    return row;
  }

  private static void styleRow(JPanel row) {
    row.setBackground(ROW_FILL);
    row.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
    row.setPreferredSize(new Dimension(100, ROW_HEIGHT));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  String getInstructionText() {
    return instruction.getText();
  }

  String getBannerText() {
    return banner.getText();
  }

  /** A green circle with a white tick, for a finished step. */
  private static final class CheckMark extends JPanel {

    private static final int SIZE = 30;

    CheckMark() {
      setOpaque(false);
      setPreferredSize(new Dimension(SIZE, SIZE));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int top = (getHeight() - SIZE) / 2;
      g.setColor(DONE_GREEN);
      g.fillOval(0, top, SIZE, SIZE);
      g.setColor(Color.WHITE);
      g.setStroke(new java.awt.BasicStroke(3f, java.awt.BasicStroke.CAP_ROUND,
          java.awt.BasicStroke.JOIN_ROUND));
      g.drawPolyline(new int[] {8, 13, 22}, new int[] {top + 15, top + 20, top + 10}, 3);
      g.dispose();
    }
  }
}
