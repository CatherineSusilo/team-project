package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The green bar along the top of every page: the course name on the left, an optional page
 * title in the middle and the Compound Lookup and View Lab Notebook links on the right. View Lab
 * Notebook is only on the dashboard: inside a module the notebook is already on screen.
 */
class HeaderBar extends JPanel {

  /**
   * Builds the bar.
   *
   * @param title the page title shown in the middle, or "" for none
   * @param openCompoundLookup opens the compound search when its link is clicked
   * @param openNotebooks opens the student's notebooks when View Lab Notebook is clicked, or
   *     null to leave the link out
   */
  HeaderBar(String title, Runnable openCompoundLookup, Runnable openNotebooks) {
    // Equal thirds keep the title centered however wide the two sides are.
    super(new GridLayout(1, 3));
    setBackground(Theme.GREEN);
    setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

    JLabel course = new JLabel("CHM136 Virtual Lab");
    course.setForeground(Theme.ON_GREEN);
    course.setFont(Theme.SANS_REGULAR.deriveFont(Theme.HEADER_SIZE));
    add(course);

    JLabel page = new JLabel(title, SwingConstants.CENTER);
    page.setForeground(Theme.ON_GREEN);
    page.setFont(Theme.SANS_REGULAR.deriveFont(Theme.HEADER_SIZE));
    add(page);

    JButton compoundLookup = Theme.linkButton("Compound Lookup", Theme.ON_GREEN);
    compoundLookup.addActionListener(event -> openCompoundLookup.run());
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    right.setOpaque(false);
    right.add(compoundLookup);
    if (openNotebooks != null) {
      JButton notebooks = Theme.linkButton("View Lab Notebook", Theme.ON_GREEN);
      notebooks.addActionListener(event -> openNotebooks.run());
      right.add(notebooks);
    }
    add(right);
  }

  /**
   * Puts this bar above a page.
   *
   * @param page what goes under the bar
   * @return the bar and the page together
   */
  JPanel above(Component page) {
    JPanel framed = new JPanel(new BorderLayout());
    framed.add(this, BorderLayout.NORTH);
    framed.add(page, BorderLayout.CENTER);
    return framed;
  }
}
