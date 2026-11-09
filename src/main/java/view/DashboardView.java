package view;

import entity.LabModule;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import usecase.LabDataAccessInterface;

/**
 * Landing page: every module the student has, with its status and actions.
 */
public class DashboardView extends JPanel {
  private static final DateTimeFormatter LAST_MODIFIED_FORMAT =
      DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");

  private final List<LabModule> modules;
  private final LabDataAccessInterface labDataAccess;
  private final Consumer<Container> showPage;
  private final JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));

  /**
   * Builds the dashboard for the given modules.
   *
   * @param modules all modules assigned to the student
   * @param labDataAccess saves progress whenever the student clicks Continue
   * @param showPage shows a page in the app window
   */
  public DashboardView(List<LabModule> modules, LabDataAccessInterface labDataAccess,
      Consumer<Container> showPage) {
    super(new BorderLayout());
    this.modules = modules;
    this.labDataAccess = labDataAccess;
    this.showPage = showPage;
    refreshCards();
    // NORTH keeps cards at their natural size instead of stretching to fill the screen.
    JPanel cardsHolder = new JPanel(new BorderLayout());
    cardsHolder.add(cards, BorderLayout.NORTH);
    add(new JScrollPane(cardsHolder), BorderLayout.CENTER);
  }

  /** Rebuilds every card so each one shows its module's current details. */
  private void refreshCards() {
    cards.removeAll();
    for (LabModule module : modules) {
      cards.add(createCard(module));
    }
    cards.revalidate();
  }

  private JPanel createCard(LabModule module) {
    JLabel title = new JLabel(module.getTitle());
    title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
    JPanel info = new JPanel(new GridLayout(0, 1, 0, 4));
    info.setOpaque(false);
    info.add(title);
    info.add(new JLabel("Due: " + module.getDueDate()));
    info.add(new JLabel("Status: " + (module.isCompleted() ? "Completed" : "To do")));
    String lastModified = module.getLastEdited()
        .map(LAST_MODIFIED_FORMAT::format)
        .orElse("NOT STARTED");
    info.add(new JLabel("Last Modified: " + lastModified));

    JButton seeInstructions = new JButton("See Instructions");
    seeInstructions.addActionListener(event -> showModulePanel(module));
    // TODO: export the completed module as PDF/CSV for submission,
    //  and enable the button only for completed modules.
    JButton export = new JButton("Export");
    export.setEnabled(false);
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttons.setOpaque(false);
    buttons.add(seeInstructions);
    buttons.add(export);

    JPanel card = new JPanel(new BorderLayout(0, 12));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
        BorderFactory.createEmptyBorder(16, 16, 16, 16)));
    card.add(info, BorderLayout.CENTER);
    card.add(buttons, BorderLayout.SOUTH);
    Dimension size = card.getPreferredSize();
    card.setPreferredSize(new Dimension(Math.max(320, size.width), size.height));
    return card;
  }

  private void showModulePanel(LabModule module) {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), module.getTitle(),
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setContentPane(new ModulePanel(module,
        () -> {
          dialog.dispose();
          openQuiz(module);
        },
        () -> {
          // TODO: reopen the pre-lab where the student left off.
        },
        () -> {
          // TODO: clear the student's saved progress for this module.
        }));
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void openQuiz(LabModule module) {
    QuizView quiz = new QuizView(module.getTitle(), module.getPreProcedureQuestions(),
        () -> showPage.accept(this),
        () -> {
          labDataAccess.saveLab(labDataAccess.getLab());
          module.setLastEdited(LocalDateTime.now());
          refreshCards();
          showPage.accept(createNextScene());
        });
    JScrollPane scroll = new JScrollPane(quiz);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    showPage.accept(scroll);
  }

  private JPanel createNextScene() {
    // TODO: build the next scene of the pre-lab; for now it's a blank page.
    JPanel scene = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
    scene.setBackground(Color.WHITE);
    JButton back = new JButton("Back to Dashboard");
    back.addActionListener(event -> showPage.accept(this));
    scene.add(back);
    return scene;
  }
}
