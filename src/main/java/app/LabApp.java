package app;

import adapter.controller.CheckQuizController;
import adapter.controller.LoadDashboardController;
import adapter.controller.SaveQuizController;
import adapter.controller.SearchCompoundController;
import adapter.controller.SubmitQuizController;
import adapter.presenter.CheckQuizPresenter;
import adapter.presenter.LoadDashboardPresenter;
import adapter.presenter.SaveQuizPresenter;
import adapter.presenter.SearchCompoundPresenter;
import adapter.presenter.SubmitQuizPresenter;
import adapter.viewmodel.CompoundViewModel;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.QuizViewModel;
import data.InMemoryLabDataAccessObject;
import data.PubChemCompoundDataAccessObject;
import data.RandomChoiceOrderer;
import java.awt.GraphicsEnvironment;
import java.time.Clock;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import usecase.checkquiz.CheckQuizInteractor;
import usecase.loaddashboard.LoadDashboardInteractor;
import usecase.savequiz.SaveQuizInteractor;
import usecase.searchcompound.SearchCompoundInteractor;
import usecase.submitquiz.SubmitQuizInteractor;
import view.CompoundSearchView;
import view.DashboardView;
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
    // TODO: create the UML's SampleAssigner and pass it to the DAO.
    InMemoryLabDataAccessObject dataAccess = new InMemoryLabDataAccessObject();

    DashboardViewModel dashboardViewModel = new DashboardViewModel();
    LoadDashboardController loadDashboardController = new LoadDashboardController(
        new LoadDashboardInteractor(dataAccess, new LoadDashboardPresenter(dashboardViewModel,
            Clock.systemDefaultZone()), new RandomChoiceOrderer(new Random())));

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

    SwingUtilities.invokeLater(() -> {
      Theme.installDefaultFonts();
      JFrame frame = new JFrame("CHM136 Virtual Lab");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      QuizView quizView = new QuizView(quizViewModel, checkQuizController,
          submitQuizController, saveQuizController);
      CompoundSearchView compoundSearchView =
          new CompoundSearchView(compoundViewModel, searchCompoundController);
      frame.setContentPane(new DashboardView(dashboardViewModel, loadDashboardController,
          quizView, compoundSearchView, page -> {
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
