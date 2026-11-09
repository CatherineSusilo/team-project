package view;

import entity.QuizQuestion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
 */
public class QuizView extends JPanel {
  private static final int TEXT_WIDTH = 700;
  private static final Color CORRECT = new Color(0, 128, 0);
  private static final Color INCORRECT = new Color(192, 0, 0);

  private final List<QuizQuestion> questions;
  private final List<List<AbstractButton>> choiceButtons = new ArrayList<>();
  private final List<JLabel> feedbackLabels = new ArrayList<>();
  private final JButton continueButton = new JButton("Continue");

  /**
   * Builds the quiz page.
   *
   * @param moduleTitle title of the module the quiz belongs to
   * @param questions the questions to ask, in order
   * @param onBack called when the student clicks "Back to Dashboard"
   * @param onContinue called when the student clicks Continue, which only appears once
   *     every answer is correct
   */
  public QuizView(String moduleTitle, List<QuizQuestion> questions, Runnable onBack,
      Runnable onContinue) {
    this.questions = questions;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

    JButton back = new JButton("Back to Dashboard");
    back.addActionListener(event -> onBack.run());
    addLeft(back);

    JLabel title = new JLabel(moduleTitle + ": Pre-Procedure Questions");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
    title.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
    addLeft(title);

    for (int i = 0; i < questions.size(); i++) {
      addLeft(createQuestion(i + 1, questions.get(i)));
    }

    JButton check = new JButton("Check Answers");
    check.addActionListener(event -> checkAnswers());
    continueButton.setVisible(false);
    continueButton.addActionListener(event -> onContinue.run());
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    actions.add(check);
    actions.add(continueButton);
    addLeft(actions);
  }

  private JPanel createQuestion(int number, QuizQuestion question) {
    JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    panel.add(new JLabel(wrap("<b>" + number + ".</b> " + question.getPrompt())),
        BorderLayout.NORTH);

    boolean images = question.getType() == QuizQuestion.Type.IMAGE;
    JPanel choicesPanel = images
        ? new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0))
        : new JPanel(new GridLayout(0, 1));
    ButtonGroup group = new ButtonGroup();
    List<AbstractButton> buttons = new ArrayList<>();
    for (String choice : question.getChoices()) {
      AbstractButton button;
      if (question.getType() == QuizQuestion.Type.MULTIPLE) {
        button = new JCheckBox(choice);
      } else {
        button = new JRadioButton(images ? imageHtml(choice) : choice);
        group.add(button);
      }
      // A changed answer hasn't been checked yet, so Continue waits for Check Answers again.
      button.addItemListener(event -> continueButton.setVisible(false));
      buttons.add(button);
      choicesPanel.add(button);
    }
    choiceButtons.add(buttons);
    panel.add(choicesPanel, BorderLayout.CENTER);

    JLabel feedback = new JLabel();
    feedbackLabels.add(feedback);
    panel.add(feedback, BorderLayout.SOUTH);
    return panel;
  }

  private void checkAnswers() {
    boolean allCorrect = true;
    for (int i = 0; i < questions.size(); i++) {
      Set<Integer> selected = new HashSet<>();
      List<AbstractButton> buttons = choiceButtons.get(i);
      for (int choice = 0; choice < buttons.size(); choice++) {
        if (buttons.get(choice).isSelected()) {
          selected.add(choice);
        }
      }
      JLabel feedback = feedbackLabels.get(i);
      if (selected.isEmpty()) {
        // Don't reveal the explanation (and so the answer) for skipped questions.
        feedback.setForeground(INCORRECT);
        feedback.setText("Not answered yet.");
        allCorrect = false;
        continue;
      }
      QuizQuestion question = questions.get(i);
      boolean correct = question.isCorrect(selected);
      allCorrect &= correct;
      feedback.setForeground(correct ? CORRECT : INCORRECT);
      feedback.setText(wrap((correct ? "<b>Correct.</b> " : "<b>Not quite.</b> ")
          + question.getExplanation()));
    }
    continueButton.setVisible(allCorrect);
    revalidate();
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
