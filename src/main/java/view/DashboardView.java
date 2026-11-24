package view;

import adapter.controller.LoadDashboardController;
import adapter.controller.SaveNotebookController;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.ModuleState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
  private final SaveNotebookController saveNotebook;
  private final QuizView quizView;
  private final CompoundSearchView compoundSearch;
  private final Consumer<Container> showPage;
  private final JPanel cards = new JPanel(new GridLayout(0, 3, 24, 24));
  private final JLabel message = new JLabel();
  /** The module the student is in, and its notebook; null on the dashboard. */
  private ModuleState openModule;
  private NotebookPanel openNotebook;

  /**
   * Builds the dashboard and loads its modules.
   *
   * @param viewModel where the modules to show appear
   * @param loadDashboard reloads the modules, e.g. after the student saves progress
   * @param saveNotebook saves a module's lab notebook
   * @param quizView the page Start opens
   * @param compoundSearch what the header's Compound Lookup link opens
   * @param showPage shows a page in the app window
   */
  public DashboardView(DashboardViewModel viewModel, LoadDashboardController loadDashboard,
      SaveNotebookController saveNotebook, QuizView quizView, CompoundSearchView compoundSearch,
      Consumer<Container> showPage) {
    super(new BorderLayout());
    this.viewModel = viewModel;
    this.loadDashboard = loadDashboard;
    this.saveNotebook = saveNotebook;
    this.quizView = quizView;
    this.compoundSearch = compoundSearch;
    this.showPage = showPage;
    viewModel.addPropertyChangeListener(this);

    JLabel heading = new JLabel("Modules", SwingConstants.CENTER);
    heading.setFont(Theme.bold(Theme.TITLE_SIZE));
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
    add(new HeaderBar("", this::showCompoundSearch, this::showNotebooks), BorderLayout.NORTH);
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
    title.setFont(Theme.SANS_REGULAR.deriveFont(Theme.HEADER_SIZE));
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
          // TODO: clear the student's saved progress and notebook for this module.
        }));
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    Theme.showDimmed(dialog);
  }

  /**
   * The View Lab Notebook popup: the modules the student has written notes for, to read and
   * edit outside the lab. Edits are saved as the student leaves each notebook.
   */
  private void showNotebooks() {
    Map<String, String> notebooks = new LinkedHashMap<>();
    for (ModuleState module : viewModel.getModules()) {
      if (!module.getNotebook().isEmpty()) {
        notebooks.put(module.getTitle(), module.getNotebook());
      }
    }
    NotebookListPanel list = new NotebookListPanel(notebooks, this::trySaveNotebook);
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Your Lab Notebooks",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setContentPane(list);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    // Blocks until the popup closes.
    Theme.showDimmed(dialog);
    // Any save error has already been shown in a popup.
    list.leaveNotebook();
    // Reload so the cards and notebooks are current.
    loadDashboard.execute();
  }

  /**
   * The compound search popup. It's the same panel every time, so the last result is still
   * there when the student opens it again.
   */
  private void showCompoundSearch() {
    // The dashboard isn't on screen while the quiz is, so ask for the window that's in front
    // instead of the dashboard's own.
    JDialog dialog = new JDialog(
        KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
        "Compound Lookup", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setContentPane(compoundSearch);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getOwner());
    Theme.showDimmed(dialog);
  }

  /**
   * Opens the quiz with the module's notebook beside it. The same notebook carries on into the
   * lab, so notes taken during the quiz are still there.
   */
  private void openQuiz(ModuleState module) {
    NotebookPanel notebook = new NotebookPanel();
    notebook.setHtml(module.getNotebook());
    openModule = module;
    openNotebook = notebook;
    HeaderBar header = new HeaderBar("Pre-Procedure Quiz", this::showCompoundSearch, null);
    quizView.open(module, () -> leaveModule(module, notebook),
        () -> showQuizComplete(module, notebook));
    JScrollPane scroll = new JScrollPane(quizView);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    showPage.accept(header.above(besideNotebook(scroll, notebook)));
  }

  /**
   * The "Good Job!" popup. Exit Module, Save Progress and closing it go to the dashboard (the
   * answers are already saved); Proceed to Experiment opens the next scene.
   */
  private void showQuizComplete(ModuleState module, NotebookPanel notebook) {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), module.getTitle(),
        Dialog.ModalityType.APPLICATION_MODAL);
    Runnable[] next = {() -> leaveModule(module, notebook)};
    dialog.setContentPane(new QuizCompletePanel(dialog::dispose, dialog::dispose, () -> {
      next[0] = () -> showLab(module, notebook);
      dialog.dispose();
    }));
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    // Blocks until the popup closes; only then swap the page.
    Theme.showDimmed(dialog);
    next[0].run();
  }

  private void showLab(ModuleState module, NotebookPanel notebook) {
    showPage.accept(new HeaderBar(module.getTitle(), this::showCompoundSearch, null)
        .above(besideNotebook(createNextScene(() -> leaveModule(module, notebook)), notebook)));
  }

  private JPanel createNextScene(Runnable backToDashboard) {
    // TODO: build the lab manual and fume hood; for now the lab is blank apart from the notebook.
    JPanel lab = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
    lab.setBackground(Color.WHITE);
    JButton back = new JButton("Back to Dashboard");
    back.addActionListener(event -> backToDashboard.run());
    lab.add(back);
    return lab;
  }

  /** A page with the module's notebook as a column on its right. */
  private static JPanel besideNotebook(Component page, NotebookPanel notebook) {
    notebook.setPreferredSize(new Dimension(320, 0));
    JPanel row = new JPanel(new BorderLayout());
    row.add(page, BorderLayout.CENTER);
    row.add(notebook, BorderLayout.EAST);
    return row;
  }

  /**
   * Saves the module's notebook if it changed, then goes back to the dashboard. If the save
   * fails, the student stays on the page with their notes, to try again.
   */
  private void leaveModule(ModuleState module, NotebookPanel notebook) {
    String html = notebook.getHtml();
    if (html.equals(module.getNotebook()) || trySaveNotebook(module.getTitle(), html)) {
      openModule = null;
      openNotebook = null;
      showDashboard();
    }
  }

  /**
   * Saves the open module's notebook before the app closes. If that fails, asks whether to
   * close anyway.
   *
   * @return whether the app can close: the notebook saved, didn't change or wasn't open, or the
   *     student chose to close without saving
   */
  public boolean saveBeforeClosing() {
    if (openNotebook == null) {
      return true;
    }
    String html = openNotebook.getHtml();
    if (html.equals(openModule.getNotebook())) {
      return true;
    }
    saveNotebook.execute(openModule.getTitle(), html);
    if (viewModel.getMessage().isEmpty()) {
      return true;
    }
    return JOptionPane.showConfirmDialog(this,
        viewModel.getMessage() + "\nClose anyway? Your notebook changes will be lost.",
        "Notebook not saved", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  /**
   * Saves a module's notebook. If that fails, says so in a popup, since the dashboard and its
   * message may not be on screen.
   *
   * @return whether the notebook was saved
   */
  private boolean trySaveNotebook(String moduleTitle, String html) {
    saveNotebook.execute(moduleTitle, html);
    String error = viewModel.getMessage();
    if (error.isEmpty()) {
      return true;
    }
    JOptionPane.showMessageDialog(
        KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(), error,
        "Notebook not saved", JOptionPane.WARNING_MESSAGE);
    return false;
  }

  /** Reloads the modules, so Progress is current, then shows the dashboard. */
  private void showDashboard() {
    loadDashboard.execute();
    showPage.accept(this);
  }
}
