package view;

import adapter.controller.LoadDashboardController;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Landing page: every module the student has, with its status and actions. The cards come
 * from {@link DashboardViewModel}, filled in by the LoadDashboard use case.
 */
public class DashboardView extends JPanel implements PropertyChangeListener {
  private final DashboardViewModel viewModel;
  private final LoadDashboardController loadDashboard;
  private final QuizView quizView;
  private final Consumer<Container> showPage;
  private final JPanel cards = new JPanel(new GridLayout(0, 3, 24, 24));
  private final JLabel message = new JLabel();

  /**
   * Builds the dashboard and loads its modules.
   *
   * @param viewModel where the modules to show appear
   * @param loadDashboard reloads the modules, e.g. after the student saves progress
   * @param quizView the page Start opens
   * @param showPage shows a page in the app window
   */
  public DashboardView(DashboardViewModel viewModel, LoadDashboardController loadDashboard,
      QuizView quizView, Consumer<Container> showPage) {
    super(new BorderLayout());
    this.viewModel = viewModel;
    this.loadDashboard = loadDashboard;
    this.quizView = quizView;
    this.showPage = showPage;
    viewModel.addPropertyChangeListener(this);

    JLabel heading = new JLabel("Modules", SwingConstants.CENTER);
    heading.setFont(Theme.bold(heading.getFont(), 28f));
    JPanel header = new JPanel(new BorderLayout(0, 8));
    header.add(heading, BorderLayout.NORTH);
    header.add(message, BorderLayout.CENTER);
    // NORTH keeps cards at their natural height instead of stretching to fill the screen.
    JPanel cardsHolder = new JPanel(new BorderLayout(0, 24));
    cardsHolder.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
    cardsHolder.add(header, BorderLayout.NORTH);
    cardsHolder.add(cards, BorderLayout.CENTER);
    JPanel top = new JPanel(new BorderLayout());
    top.add(cardsHolder, BorderLayout.NORTH);
    add(new HeaderBar(""), BorderLayout.NORTH);
    add(new JScrollPane(top), BorderLayout.CENTER);
    loadDashboard.execute();
  }

  /** Rebuilds every card whenever the LoadDashboard presenter updates the view model. */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    message.setText(viewModel.getMessage());
    message.setVisible(!viewModel.getMessage().isEmpty());
    cards.removeAll();
    for (ModuleState module : viewModel.getModules()) {
      cards.add(createCard(module));
    }
    cards.revalidate();
    cards.repaint();
  }

  private JPanel createCard(ModuleState module) {
    JLabel title = new JLabel(module.getTitle());
    title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
    JPanel info = new JPanel(new GridLayout(0, 1, 0, 4));
    info.setOpaque(false);
    info.add(title);
    info.add(new JLabel("Due: " + module.getDueDate()));
    JLabel progress = new JLabel(module.getProgress());
    progress.setForeground(module.isComplete() ? Theme.GREEN : Theme.RED);
    JPanel progressRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    progressRow.setOpaque(false);
    progressRow.add(new JLabel("Progress:"));
    progressRow.add(progress);
    info.add(progressRow);

    JButton seeInstructions =
        Theme.filledButton("See Instructions", Theme.GREEN, Theme.ON_GREEN);
    seeInstructions.addActionListener(event -> showModulePanel(module));
    // TODO: export the completed module as PDF/CSV for submission.
    JButton export = Theme.filledButton("Export",
        module.isComplete() ? Theme.YELLOW : Theme.DISABLED, Color.BLACK);
    export.setEnabled(module.isComplete());
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
    return card;
  }

  private void showModulePanel(ModuleState module) {
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

  private void openQuiz(ModuleState module) {
    HeaderBar header = new HeaderBar(module.getTitle());
    quizView.open(module, this::showDashboard,
        () -> showPage.accept(new HeaderBar(module.getTitle()).above(createNextScene())));
    JScrollPane scroll = new JScrollPane(quizView);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    showPage.accept(header.above(scroll));
  }

  private JPanel createNextScene() {
    // TODO: build the next scene of the pre-lab; for now it's a blank page.
    JPanel scene = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
    scene.setBackground(Color.WHITE);
    JButton back = new JButton("Back to Dashboard");
    back.addActionListener(event -> showDashboard());
    scene.add(back);
    return scene;
  }

  /** Reloads the modules, so Progress is current, then shows the dashboard. */
  private void showDashboard() {
    loadDashboard.execute();
    showPage.accept(this);
  }
}
