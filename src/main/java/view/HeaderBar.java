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
 * title in the middle and the View Lab Notebook link on the right.
 */
class HeaderBar extends JPanel {

  /**
   * Builds the bar.
   *
   * @param title the page title shown in the middle, or "" for none
   */
  HeaderBar(String title) {
    // Equal thirds keep the title centered however wide the two sides are.
    super(new GridLayout(1, 3));
    setBackground(Theme.GREEN);
    setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

    JLabel course = new JLabel("CHM136 Virtual Lab");
    course.setForeground(Theme.ON_GREEN);
    course.setFont(Theme.bold(course.getFont(), 18f));
    add(course);

    JLabel page = new JLabel(title, SwingConstants.CENTER);
    page.setForeground(Theme.ON_GREEN);
    page.setFont(Theme.bold(page.getFont(), 16f));
    add(page);

    JButton notebook = Theme.linkButton("View Lab Notebook", Theme.ON_GREEN);
    notebook.addActionListener(event -> {
      // TODO: open the student's lab notebook once it exists.
    });
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    right.setOpaque(false);
    right.add(notebook);
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
