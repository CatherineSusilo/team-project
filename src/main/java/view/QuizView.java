package view;

import adapter.controller.CheckQuizController;
import adapter.viewmodel.FeedbackState;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.QuestionState;
import adapter.viewmodel.QuizViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Pre-procedure quiz page: every question on one page, with a button to check all answers.
 * Checking goes through {@link CheckQuizController}; the results come back in
 * {@link QuizViewModel}.
 */
public class QuizView extends JPanel implements PropertyChangeListener {
  private static final int TEXT_WIDTH = 700;
  private static final Color CORRECT = new Color(0, 128, 0);
  private static final Color INCORRECT = new Color(192, 0, 0);

  private final QuizViewModel viewModel;
  private final CheckQuizController controller;
  private final List<List<AbstractButton>> choiceButtons = new ArrayList<>();
  private final List<JLabel> feedbackLabels = new ArrayList<>();
  private final JButton continueButton = new JButton("Continue");
  private final JLabel message = new JLabel();
  private String moduleTitle = "";
  private Runnable onSubmitted = () -> { };

  /**
   * Builds an empty quiz page; {@link #open} fills it in.
   *
   * @param viewModel where the check results appear
   * @param controller checks and submits the answers
   */
  public QuizView(QuizViewModel viewModel, CheckQuizController controller) {
    this.viewModel = viewModel;
    this.controller = controller;
    viewModel.addPropertyChangeListener(this);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    continueButton.addActionListener(event -> controller.execute(moduleTitle, answers(), true));
    message.setForeground(INCORRECT);
  }

  /**
   * Shows the quiz for a module, replacing any quiz shown before.
   *
   * @param module the module whose quiz to show
   * @param onBack called when the student clicks "Back to Dashboard"
   * @param onSubmitted called once Continue has saved the student's progress
   */
  public void open(ModuleState module, Runnable onBack, Runnable onSubmitted) {
    this.moduleTitle = module.getTitle();
    this.onSubmitted = onSubmitted;
    removeAll();
    choiceButtons.clear();
    feedbackLabels.clear();

    JButton back = new JButton("Back to Dashboard");
    back.addActionListener(event -> onBack.run());
    addLeft(back);

    JLabel title = new JLabel(module.getTitle() + ": Pre-Procedure Questions");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
    title.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
    addLeft(title);

    List<QuestionState> questions = module.getQuestions();
    for (int i = 0; i < questions.size(); i++) {
      addLeft(createQuestion(i + 1, questions.get(i)));
    }

    JButton check = new JButton("Check Answers");
    check.addActionListener(event -> controller.execute(moduleTitle, answers(), false));
    continueButton.setVisible(false);
    message.setText("");
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    actions.add(check);
    actions.add(continueButton);
    addLeft(actions);
    addLeft(message);
    revalidate();
    repaint();
  }

  /** Redraws the feedback whenever the CheckQuiz presenter updates the view model. */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    List<FeedbackState> feedback = viewModel.getFeedback();
    if (feedback.size() == feedbackLabels.size()) {
      for (int i = 0; i < feedback.size(); i++) {
        FeedbackState state = feedback.get(i);
        JLabel label = feedbackLabels.get(i);
        label.setForeground(state.isCorrect() ? CORRECT : INCORRECT);
        label.setText(wrap("<b>" + state.getVerdict() + "</b> " + state.getExplanation()));
      }
    }
    continueButton.setVisible(viewModel.isContinueVisible());
    message.setText(viewModel.getMessage());
    revalidate();
    repaint();
    if (viewModel.isSubmitted()) {
      onSubmitted.run();
    }
  }

  private JPanel createQuestion(int number, QuestionState question) {
    JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    panel.add(new JLabel(wrap("<b>" + number + ".</b> " + question.getPrompt())),
        BorderLayout.NORTH);

    JLabel feedback = new JLabel();
    feedbackLabels.add(feedback);

    boolean images = "IMAGE".equals(question.getType());
    JPanel choicesPanel = images
        ? new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0))
        : new JPanel(new GridLayout(0, 1));
    ButtonGroup group = new ButtonGroup();
    List<AbstractButton> buttons = new ArrayList<>();
    for (String choice : question.getChoices()) {
      AbstractButton button;
      if ("MULTIPLE".equals(question.getType())) {
        button = new JCheckBox(choice);
      } else {
        button = new JRadioButton(images ? imageHtml(choice) : choice);
        group.add(button);
      }
      // A changed answer hasn't been checked yet: clear its old verdict, and Continue waits
      // for Check Answers again.
      button.addItemListener(event -> {
        continueButton.setVisible(false);
        feedback.setText("");
      });
      buttons.add(button);
      choicesPanel.add(button);
    }
    choiceButtons.add(buttons);
    panel.add(choicesPanel, BorderLayout.CENTER);
    panel.add(feedback, BorderLayout.SOUTH);
    return panel;
  }

  /** For each question, the indexes of the choices the student selected. */
  private List<Set<Integer>> answers() {
    List<Set<Integer>> answers = new ArrayList<>();
    for (List<AbstractButton> buttons : choiceButtons) {
      Set<Integer> selected = new HashSet<>();
      for (int choice = 0; choice < buttons.size(); choice++) {
        if (buttons.get(choice).isSelected()) {
          selected.add(choice);
        }
      }
      answers.add(selected);
    }
    return answers;
  }

  private void addLeft(JComponent component) {
    component.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(component);
  }

  private static String wrap(String html) {
    return "<html><div style='width: " + TEXT_WIDTH + "px'>" + html + "</div></html>";
  }

  private static String imageHtml(String resourcePath) {
    URL url = Objects.requireNonNull(QuizView.class.getResource(resourcePath),
        "Missing image resource " + resourcePath);
    return "<html><img src='" + url + "'></html>";
  }
}
