package view;

import adapter.controller.CheckQuizController;
import adapter.controller.SaveQuizController;
import adapter.controller.SubmitQuizController;
import adapter.viewmodel.FeedbackState;
import adapter.viewmodel.ModuleState;
import adapter.viewmodel.QuestionState;
import adapter.viewmodel.QuizViewModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Pre-procedure quiz page, one question at a time: a sidebar of questions on the left, the
 * current question's card in the middle, and Exit Module, Back and Next / Submit Quiz along
 * the bottom. Opening it restores the student's saved answers and jumps to the first question
 * not yet answered correctly.
 *
 * <p>Check Answer goes through {@link CheckQuizController}, Submit Quiz through
 * {@link SubmitQuizController} and Exit Module through {@link SaveQuizController}; the results
 * come back in {@link QuizViewModel}.
 */
public class QuizView extends JPanel implements PropertyChangeListener {
  private static final int TEXT_WIDTH = 600;

  private final QuizViewModel viewModel;
  private final CheckQuizController checkQuiz;
  private final SubmitQuizController submitQuiz;
  private final SaveQuizController saveQuiz;

  /** For each question, reads the indexes of the choices the student selected. */
  private final List<Supplier<Set<Integer>>> answerReaders = new ArrayList<>();
  private final List<JLabel> feedbackLabels = new ArrayList<>();
  private final List<JButton> sidebarButtons = new ArrayList<>();
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel cards = new JPanel(cardLayout);
  private final JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 8));
  private final JButton back = Theme.filledButton("← Back", Theme.DISABLED, Color.BLACK);
  private final JButton next = Theme.filledButton("Next →", Theme.GREEN, Theme.ON_GREEN);
  private final JLabel message = new JLabel();
  private String moduleTitle = "";
  private int current;
  /** The question whose Check Answer was clicked last, or -1 if none. */
  private int checking = -1;
  private Runnable onBack = () -> { };
  private Runnable onSubmitted = () -> { };

  /**
   * Builds an empty quiz page; {@link #open} fills it in.
   *
   * @param viewModel where the results appear
   * @param checkQuiz checks the answers (Check Answer)
   * @param submitQuiz saves progress once every answer is correct (Submit Quiz)
   * @param saveQuiz saves the answers so far, right or wrong (Exit Module)
   */
  public QuizView(QuizViewModel viewModel, CheckQuizController checkQuiz,
      SubmitQuizController submitQuiz, SaveQuizController saveQuiz) {
    super(new BorderLayout(24, 0));
    this.viewModel = viewModel;
    this.checkQuiz = checkQuiz;
    this.submitQuiz = submitQuiz;
    this.saveQuiz = saveQuiz;
    viewModel.addPropertyChangeListener(this);
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

    back.addActionListener(event -> show(current - 1));
    next.addActionListener(event -> {
      if (current == answerReaders.size() - 1) {
        submitQuiz.execute(moduleTitle, answers());
      } else {
        show(current + 1);
      }
    });
    JButton exit = Theme.linkButton("Exit Module", Theme.GREEN);
    exit.addActionListener(event -> saveQuiz.execute(moduleTitle, answers()));
    message.setForeground(Theme.RED);

    JPanel arrows = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
    arrows.add(back);
    arrows.add(next);
    JPanel bottom = new JPanel(new BorderLayout(0, 8));
    bottom.add(message, BorderLayout.NORTH);
    bottom.add(exit, BorderLayout.WEST);
    bottom.add(arrows, BorderLayout.EAST);

    cards.setBackground(Color.WHITE);
    cards.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Theme.DISABLED, 1, true),
        BorderFactory.createEmptyBorder(24, 24, 24, 24)));
    JPanel main = new JPanel(new BorderLayout(0, 16));
    main.add(cards, BorderLayout.CENTER);
    main.add(bottom, BorderLayout.SOUTH);

    // NORTH keeps the sidebar buttons at their natural height.
    JPanel sidebarHolder = new JPanel(new BorderLayout());
    sidebarHolder.add(sidebar, BorderLayout.NORTH);
    add(sidebarHolder, BorderLayout.WEST);
    add(main, BorderLayout.CENTER);
  }

  /**
   * Shows the quiz for a module, replacing any quiz shown before. Restores the module's saved
   * answers and starts on the first question not yet answered correctly.
   *
   * @param module the module whose quiz to show
   * @param onBack called once Exit Module has saved the student's answers
   * @param onSubmitted called once Submit Quiz has saved the student's progress
   */
  public void open(ModuleState module, Runnable onBack, Runnable onSubmitted) {
    this.moduleTitle = module.getTitle();
    this.onBack = onBack;
    this.onSubmitted = onSubmitted;
    cards.removeAll();
    sidebar.removeAll();
    answerReaders.clear();
    feedbackLabels.clear();
    sidebarButtons.clear();
    checking = -1;

    List<QuestionState> questions = module.getQuestions();
    List<Set<Integer>> saved = module.getSavedAnswers();
    int firstUnfinished = -1;
    for (int i = 0; i < questions.size(); i++) {
      Set<Integer> answer = i < saved.size() ? saved.get(i) : Set.of();
      cards.add(createQuestion(i, questions.get(i), answer), String.valueOf(i));
      if (firstUnfinished < 0 && !questions.get(i).isSavedCorrect()) {
        firstUnfinished = i;
      }
      int question = i;
      JButton button = Theme.filledButton("Question " + (i + 1), Theme.DISABLED, Color.BLACK);
      button.addActionListener(event -> show(question));
      sidebarButtons.add(button);
      sidebar.add(button);
    }
    // Everything already correct: start from the top.
    show(Math.max(firstUnfinished, 0));
  }

  /** Shows the feedback for the question just checked, or moves on once saved or submitted. */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    List<FeedbackState> feedback = viewModel.getFeedback();
    if (checking >= 0 && checking < feedback.size()) {
      FeedbackState state = feedback.get(checking);
      JLabel label = feedbackLabels.get(checking);
      label.setForeground(state.isCorrect() ? Theme.GREEN : Theme.RED);
      label.setText(wrap("<b>" + state.getVerdict() + "</b> " + state.getExplanation()));
      checking = -1;
    }
    message.setText(viewModel.getMessage());
    revalidate();
    repaint();
    if (viewModel.isSubmitted()) {
      onSubmitted.run();
    } else if (viewModel.isSaved()) {
      onBack.run();
    }
  }

  /** Shows one question: its card, its sidebar highlight, and the right arrows. */
  private void show(int question) {
    if (question < 0 || question >= answerReaders.size()) {
      return;
    }
    current = question;
    cardLayout.show(cards, String.valueOf(question));
    for (int i = 0; i < sidebarButtons.size(); i++) {
      sidebarButtons.get(i).setBackground(i == question ? Theme.GREEN : Theme.DISABLED);
      sidebarButtons.get(i).setForeground(i == question ? Theme.ON_GREEN : Color.BLACK);
    }
    back.setEnabled(question > 0);
    next.setText(question == answerReaders.size() - 1 ? "Submit Quiz" : "Next →");
    message.setText("");
    revalidate();
    repaint();
  }

  /** "Question N", the prompt, the answer area, Check Answer and the feedback. */
  private JPanel createQuestion(int index, QuestionState question, Set<Integer> saved) {
    JLabel heading = new JLabel("Question " + (index + 1));
    heading.setFont(Theme.bold(heading.getFont(), 20f));
    JPanel top = new JPanel(new BorderLayout(0, 8));
    top.setOpaque(false);
    top.add(heading, BorderLayout.NORTH);
    top.add(new JLabel(wrap(question.getPrompt())), BorderLayout.CENTER);

    JLabel feedback = new JLabel();
    feedbackLabels.add(feedback);
    // A changed answer hasn't been checked yet, so its old verdict no longer applies.
    Runnable onChange = () -> feedback.setText("");
    JPanel answer = question.isEquation() ? createEquation(question, saved, onChange)
        : createChoices(question, saved, onChange);
    answer.setOpaque(false);

    JButton check = Theme.filledButton("Check Answer", Theme.GREEN, Theme.ON_GREEN);
    check.addActionListener(event -> {
      checking = index;
      checkQuiz.execute(moduleTitle, answers());
    });
    JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    checkRow.setOpaque(false);
    checkRow.add(check);
    JPanel bottom = new JPanel(new BorderLayout(0, 8));
    bottom.setOpaque(false);
    bottom.add(checkRow, BorderLayout.NORTH);
    bottom.add(feedback, BorderLayout.CENTER);

    JPanel panel = new JPanel(new BorderLayout(0, 16));
    panel.setOpaque(false);
    panel.add(top, BorderLayout.NORTH);
    panel.add(answer, BorderLayout.CENTER);
    panel.add(bottom, BorderLayout.SOUTH);
    // NORTH keeps the card's content at the top instead of spreading it down.
    JPanel holder = new JPanel(new BorderLayout());
    holder.setOpaque(false);
    holder.add(panel, BorderLayout.NORTH);
    return holder;
  }

  private JPanel createChoices(QuestionState question, Set<Integer> saved, Runnable onChange) {
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
      button.setSelected(saved.contains(buttons.size()));
      button.setOpaque(false);
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
   * empty blank and greys the tile out; tapping a filled blank empties it. Saved tiles start
   * in the blanks.
   */
  private JPanel createEquation(QuestionState question, Set<Integer> saved, Runnable onChange) {
    List<JButton> tiles = new ArrayList<>();
    JButton[] blanks = new JButton[question.getBlankCount()];
    int[] filled = new int[blanks.length];
    Arrays.fill(filled, -1);
    int nextBlank = 0;
    for (int tile : new TreeSet<>(saved)) {
      if (nextBlank < filled.length) {
        filled[nextBlank++] = tile;
      }
    }

    JPanel equation = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    equation.add(new JLabel(question.getReactants() + " →"));
    for (int i = 0; i < blanks.length; i++) {
      if (i > 0) {
        equation.add(new JLabel("+"));
      }
      int blank = i;
      blanks[i] = new JButton(filled[i] >= 0 ? question.getChoices().get(filled[i]) : "?");
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
      button.setEnabled(Arrays.stream(filled).noneMatch(used -> used == tile));
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

    equation.setOpaque(false);
    tilesPanel.setOpaque(false);
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

  private static String wrap(String html) {
    return "<html><div style='width: " + TEXT_WIDTH + "px'>" + html + "</div></html>";
  }

  private static String imageHtml(String resourcePath) {
    URL url = Objects.requireNonNull(QuizView.class.getResource(resourcePath),
        "Missing image resource " + resourcePath);
    return "<html><img src='" + url + "'></html>";
  }
}
