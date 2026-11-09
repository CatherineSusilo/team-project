package app;

import data.InMemoryLabDataAccessObject;
import entity.LabModule;
import java.awt.GraphicsEnvironment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import view.DashboardView;

/**
 * Launches the app on the dashboard page.
 */
public class LabApp {

  /**
   * Opens the dashboard window.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    // TODO: load the student's modules and progress from a real data source.
    // TODO: replace the placeholder due date with the real one.
    List<LabModule> modules = List.of(
        new LabModule("Pre-lab 3: Extraction Lab", LocalDate.of(2026, 11, 30),
            "You are QA at Lash Miller Inc. Two API batches (acetylsalicylic acid / aspirin, "
                + "or mefenamic acid / Ponstel) are contaminated with vanillin and mislabeled. "
                + "Purify your assigned unknown batch using liquid-liquid extraction and "
                + "identify which API it is.\n\n"
                + "Learning objectives:\n"
                + "- Separate a mixture of solids using liquid-liquid extraction\n"
                + "- Isolate a targeted solid compound via vacuum filtration\n"
                + "- Characterize an isolated compound using melting point and IR data\n"
                + "- Understand how acid/base chemistry alters solubility in organic vs. "
                + "aqueous solvents\n"
                + "- Understand how extractions are used to isolate a compound in organic "
                + "chemistry",
            false,
            // Sample value; null means not started: shows "NOT STARTED" and hides Continue.
            LocalDateTime.of(2026, 11, 5, 14, 30),
            PreLab3Quiz.questions()));

    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("CHM136 Virtual Lab");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new DashboardView(modules, new InMemoryLabDataAccessObject(), page -> {
        frame.setContentPane(page);
        frame.getRootPane().revalidate();
        frame.getRootPane().repaint();
      }));
      frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      frame.setVisible(true);
    });
  }
}
