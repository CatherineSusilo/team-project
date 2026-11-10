package app;

import adapter.controller.CheckQuizController;
import adapter.controller.LoadDashboardController;
import adapter.controller.SaveQuizController;
import adapter.controller.SubmitQuizController;
import adapter.presenter.CheckQuizPresenter;
import adapter.presenter.LoadDashboardPresenter;
import adapter.presenter.SaveQuizPresenter;
import adapter.presenter.SubmitQuizPresenter;
import adapter.viewmodel.DashboardViewModel;
import adapter.viewmodel.QuizViewModel;
import data.InMemoryLabDataAccessObject;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import usecase.checkquiz.CheckQuizInteractor;
import usecase.loaddashboard.LoadDashboardInteractor;
import usecase.savequiz.SaveQuizInteractor;
import usecase.submitquiz.SubmitQuizInteractor;
import view.DashboardView;
import view.QuizView;

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
        new LoadDashboardInteractor(dataAccess, new LoadDashboardPresenter(dashboardViewModel)));

    QuizViewModel quizViewModel = new QuizViewModel();
    CheckQuizController checkQuizController = new CheckQuizController(
        new CheckQuizInteractor(dataAccess, new CheckQuizPresenter(quizViewModel)));
    SubmitQuizController submitQuizController = new SubmitQuizController(
        new SubmitQuizInteractor(dataAccess, dataAccess, new SubmitQuizPresenter(quizViewModel)));
    SaveQuizController saveQuizController = new SaveQuizController(
        new SaveQuizInteractor(dataAccess, new SaveQuizPresenter(quizViewModel)));

    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("CHM136 Virtual Lab");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      QuizView quizView = new QuizView(quizViewModel, checkQuizController,
          submitQuizController, saveQuizController);
      frame.setContentPane(new DashboardView(dashboardViewModel, loadDashboardController,
          quizView, page -> {
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
