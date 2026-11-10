package view;

import adapter.controller.CheckQuizController;
import adapter.controller.SubmitQuizController;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
  private final CheckQuizController checkQuiz;
  private final SubmitQuizController submitQuiz;

  /** For each question, reads the indexes of the choices the student selected. */
  private final List<Supplier<Set<Integer>>> answerReaders = new ArrayList<>();
  private final List<JLabel> feedbackLabels = new ArrayList<>();
  private final JButton continueButton = new JButton("Continue");
  private final JLabel message = new JLabel();
  private String moduleTitle = "";
  private Runnable onSubmitted = () -> { };

  /**
   * Builds an empty quiz page; {@link #open} fills it in.
   *
   * @param viewModel where the check results appear
   * @param checkQuiz checks the answers (Check Answers)
   * @param submitQuiz saves progress (Continue)
   */
  public QuizView(QuizViewModel viewModel, CheckQuizController checkQuiz,
      SubmitQuizController submitQuiz) {
    this.viewModel = viewModel;
    this.checkQuiz = checkQuiz;
    this.submitQuiz = submitQuiz;
    viewModel.addPropertyChangeListener(this);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    continueButton.addActionListener(event -> submitQuiz.execute(moduleTitle, answers()));
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
    // TODO: save quiz progress: pre-select module's saved answers (ticks, radios, filled
    //  blanks) and jump to the first question that isn't answered correctly yet.
    this.moduleTitle = module.getTitle();
    this.onSubmitted = onSubmitted;
    removeAll();
    answerReaders.clear();
    feedbackLabels.clear();

    JButton back = new JButton("Back to Dashboard");
    // TODO: save quiz progress: call a SaveQuizController with answers() first (no
    //  all-correct check), and leave once the view model says the answers were saved.
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
    check.addActionListener(event -> checkQuiz.execute(moduleTitle, answers()));
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
    // A changed answer hasn't been checked yet: 
    // clear its old verdict, and Continue waits for Check Answers again.
    Runnable onChange = () -> {
      continueButton.setVisible(false);
      feedback.setText("");
    };
    panel.add(question.isEquation() ? createEquation(question, onChange)
        : createChoices(question, onChange), BorderLayout.CENTER);
    panel.add(feedback, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createChoices(QuestionState question, Runnable onChange) {
    boolean images = question.isImageChoices();
    JPanel choicesPanel = images
        ? new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0))
        : new JPanel(new GridLayout(0, 1));
    ButtonGroup group = new ButtonGroup();
    List<AbstractButton> buttons = new ArrayList<>();
    for (String choice : question.getChoices()) {
      AbstractButton button;
      if (question.isMultipleChoice()) {
        button = new JCheckBox(choice);
      } else {
        button = new JRadioButton(images ? imageHtml(choice) : choice);
        group.add(button);
      }
      button.addItemListener(event -> onChange.run());
      buttons.add(button);
      choicesPanel.add(button);
    }
    answerReaders.add(() -> {
      Set<Integer> selected = new HashSet<>();
      for (int choice = 0; choice < buttons.size(); choice++) {
        if (buttons.get(choice).isSelected()) {
          selected.add(choice);
        }
      }
      return selected;
    });
    return choicesPanel;
  }

  /**
   * The reactants, one blank per product, and the product tiles. Tapping a tile fills the next
   * empty blank and greys the tile out; tapping a filled blank empties it.
   */
  private JPanel createEquation(QuestionState question, Runnable onChange) {
    List<JButton> tiles = new ArrayList<>();
    JButton[] blanks = new JButton[question.getBlankCount()];
    int[] filled = new int[blanks.length];
    // TODO: save quiz progress: start from the saved tile indexes instead of all empty, and
    //  disable those tiles.
    Arrays.fill(filled, -1);

    JPanel equation = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    equation.add(new JLabel(question.getReactants() + " →"));
    for (int i = 0; i < blanks.length; i++) {
      if (i > 0) {
        equation.add(new JLabel("+"));
      }
      int blank = i;
      blanks[i] = new JButton("?");
      blanks[i].addActionListener(event -> {
        if (filled[blank] >= 0) {
          tiles.get(filled[blank]).setEnabled(true);
          filled[blank] = -1;
          blanks[blank].setText("?");
          onChange.run();
        }
      });
      equation.add(blanks[i]);
    }

    JPanel tilesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    List<String> products = question.getChoices();
    for (int i = 0; i < products.size(); i++) {
      int tile = i;
      JButton button = new JButton(products.get(i));
      button.addActionListener(event -> {
        for (int blank = 0; blank < blanks.length; blank++) {
          if (filled[blank] < 0) {
            filled[blank] = tile;
            blanks[blank].setText(products.get(tile));
            button.setEnabled(false);
            onChange.run();
            return;
          }
        }
      });
      tiles.add(button);
      tilesPanel.add(button);
    }
    answerReaders.add(() -> Arrays.stream(filled).filter(tile -> tile >= 0).boxed()
        .collect(Collectors.toSet()));

    JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
    panel.add(equation);
    panel.add(new JLabel("Tap a product to fill the next blank. Tap a blank to clear it."));
    panel.add(tilesPanel);
    return panel;
  }

  /** For each question, the indexes of the choices the student selected. */
  private List<Set<Integer>> answers() {
    List<Set<Integer>> answers = new ArrayList<>();
    for (Supplier<Set<Integer>> reader : answerReaders) {
      answers.add(reader.get());
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
