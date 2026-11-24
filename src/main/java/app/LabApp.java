package app;

import adapter.controller.CheckQuizController;
import adapter.controller.LoadDashboardController;
import adapter.controller.MoveItemController;
import adapter.controller.NavigateStepController;
import adapter.controller.SaveNotebookController;
import adapter.controller.SaveQuizController;
import adapter.controller.SearchCompoundController;
import adapter.controller.SpawnItemController;
import adapter.controller.SubmitQuizController;
import adapter.presenter.CheckQuizPresenter;
import adapter.presenter.LoadDashboardPresenter;
import adapter.presenter.MoveItemPresenter;
import adapter.presenter.NavigateStepPresenter;
import adapter.presenter.SaveNotebookPresenter;
import adapter.presenter.SaveQuizPresenter;
import adapter.presenter.SearchCompoundPresenter;
import adapter.presenter.SpawnItemPresenter;
import adapter.presenter.SubmitQuizPresenter;
import adapter.viewmodel.CompoundViewModel;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.LabViewModel;
import adapter.viewmodel.QuizViewModel;
import data.JsonLabDataAccessObject;
import data.PubChemCompoundDataAccessObject;
import data.RandomChoiceOrderer;
import entity.ItemFactory;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.desktop.QuitStrategy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import usecase.LabStateMapper;
import usecase.checkquiz.CheckQuizInteractor;
import usecase.loaddashboard.LoadDashboardInteractor;
import usecase.moveitem.MoveItemInteractor;
import usecase.navigatestep.NavigateStepInteractor;
import usecase.savenotebook.SaveNotebookInteractor;
import usecase.savequiz.SaveQuizInteractor;
import usecase.searchcompound.SearchCompoundInteractor;
import usecase.spawnitem.SpawnItemInteractor;
import usecase.submitquiz.SubmitQuizInteractor;
import view.CompoundSearchView;
import view.DashboardView;
import view.LabView;
import view.QuizView;
import view.Theme;

/**
 * Builds the DAO, interactors, presenters, controllers and views, then opens the dashboard.
 */
public class LabApp {

  /**
   * Wires the app together and opens the dashboard window.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    JsonLabDataAccessObject dataAccess = new JsonLabDataAccessObject(
        Path.of(System.getProperty("user.home"), ".chm136-virtual-lab", "save.json"));

    DashboardViewModel dashboardViewModel = new DashboardViewModel();
    LoadDashboardController loadDashboardController = new LoadDashboardController(
        new LoadDashboardInteractor(dataAccess, new LoadDashboardPresenter(dashboardViewModel,
            Clock.systemDefaultZone()), new RandomChoiceOrderer(new Random())));
    SaveNotebookController saveNotebookController = new SaveNotebookController(
        new SaveNotebookInteractor(dataAccess, new SaveNotebookPresenter(dashboardViewModel)));

    QuizViewModel quizViewModel = new QuizViewModel();
    CheckQuizController checkQuizController = new CheckQuizController(
        new CheckQuizInteractor(dataAccess, new CheckQuizPresenter(quizViewModel)));
    SubmitQuizController submitQuizController = new SubmitQuizController(
        new SubmitQuizInteractor(dataAccess, dataAccess, new SubmitQuizPresenter(quizViewModel)));
    SaveQuizController saveQuizController = new SaveQuizController(
        new SaveQuizInteractor(dataAccess, new SaveQuizPresenter(quizViewModel)));

    CompoundViewModel compoundViewModel = new CompoundViewModel();
    SearchCompoundController searchCompoundController = new SearchCompoundController(
        new SearchCompoundInteractor(new PubChemCompoundDataAccessObject(),
            new SearchCompoundPresenter(compoundViewModel)));

    LabViewModel labViewModel = new LabViewModel();
    labViewModel.setState(LabStateMapper.from(dataAccess.getLab()));
    NavigateStepController navigateStepController = new NavigateStepController(
        new NavigateStepInteractor(dataAccess, new NavigateStepPresenter(labViewModel)));
    SpawnItemController spawnItemController = new SpawnItemController(
        new SpawnItemInteractor(dataAccess, new SpawnItemPresenter(labViewModel),
            new ItemFactory()));
    MoveItemController moveItemController = new MoveItemController(
        new MoveItemInteractor(dataAccess, new MoveItemPresenter(labViewModel)));

    SwingUtilities.invokeLater(() -> {
      Theme.installDefaultFonts();
      JFrame frame = new JFrame("CHM136 Virtual Lab");
      // Closing goes through the dashboard first, so an open notebook is saved.
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      if (Desktop.isDesktopSupported()
          && Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_STRATEGY)) {
        // Make Cmd+Q on a Mac close the windows (and save) instead of quitting straight away.
        Desktop.getDesktop().setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
      }
      QuizView quizView = new QuizView(quizViewModel, checkQuizController,
          submitQuizController, saveQuizController);
      CompoundSearchView compoundSearchView =
          new CompoundSearchView(compoundViewModel, searchCompoundController);
      DashboardView dashboard = new DashboardView(dashboardViewModel, loadDashboardController,
          saveNotebookController, quizView, compoundSearchView, page -> {
            frame.setContentPane(page);
            frame.getRootPane().revalidate();
            frame.getRootPane().repaint();
          }, new LabView(labViewModel, navigateStepController, spawnItemController,
              moveItemController));
      frame.setContentPane(dashboard);
      frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent event) {
          if (dashboard.saveBeforeClosing()) {
            System.exit(0);
          }
        }
      });
      frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      frame.setVisible(true);
    });
  }
}
