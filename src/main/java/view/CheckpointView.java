package view;

import adapter.controller.AnswerCheckpointController;
import adapter.controller.ShowCheckpointController;
import adapter.viewmodel.CheckpointViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * The pause shown after each procedure step: one question, Check Answer, and a Continue button
 * that stays disabled until the student answers correctly and so confirms they understood.
 *
 * <p>Check Answer goes through {@link AnswerCheckpointController}; {@link #open} loads the
 * question through {@link ShowCheckpointController}. Results come back in
 * {@link CheckpointViewModel}.
 */
public class CheckpointView extends JPanel implements PropertyChangeListener {
  private static final int TEXT_WIDTH = 600;

  private final CheckpointViewModel viewModel;
  private final AnswerCheckpointController answerCheckpoint;
  private final ShowCheckpointController showCheckpoint;

  private final JLabel heading = new JLabel();
  private final JLabel prompt = new JLabel();
  private final JPanel choicesPanel = new JPanel(new GridLayout(0, 1));
  private final List<AbstractButton> choiceButtons = new ArrayList<>();
  private final JLabel feedback = new JLabel();
  private final JLabel message = new JLabel();
  private final JButton check = Theme.filledButton("Check Answer", Theme.GREEN, Theme.ON_GREEN);
  private final JButton next = Theme.filledButton("I understand, continue →", Theme.GREEN,
      Theme.ON_GREEN);
  private Runnable onContinue = () -> { };

  /**
   * Builds an empty checkpoint page; {@link #open} fills it in.
   *
   * @param viewModel where the question and results appear
   * @param showCheckpoint loads the question
   * @param answerCheckpoint checks the answer (Check Answer)
   */
  public CheckpointView(CheckpointViewModel viewModel, ShowCheckpointController showCheckpoint,
      AnswerCheckpointController answerCheckpoint) {
    super(new BorderLayout(0, 16));
    this.viewModel = viewModel;
    this.showCheckpoint = showCheckpoint;
    this.answerCheckpoint = answerCheckpoint;
    viewModel.addPropertyChangeListener(this);
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

    heading.setFont(Theme.bold(heading.getFont(), 20f));
    message.setForeground(Theme.RED);
    choicesPanel.setOpaque(false);

    check.addActionListener(event ->
        answerCheckpoint.execute(viewModel.getStepIndex(), selected()));
    next.setEnabled(false);
    next.setBackground(Theme.DISABLED);
    next.setForeground(Color.BLACK);
    next.addActionListener(event -> onContinue.run());

    JPanel top = new JPanel(new BorderLayout(0, 8));
    top.setOpaque(false);
    top.add(heading, BorderLayout.NORTH);
    top.add(prompt, BorderLayout.CENTER);

    JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    checkRow.setOpaque(false);
    checkRow.add(check);
    JPanel feedbackArea = new JPanel(new BorderLayout(0, 8));
    feedbackArea.setOpaque(false);
    feedbackArea.add(checkRow, BorderLayout.NORTH);
    feedbackArea.add(feedback, BorderLayout.CENTER);
    feedbackArea.add(message, BorderLayout.SOUTH);

    JPanel card = new JPanel(new BorderLayout(0, 16));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Theme.DISABLED, 1, true),
        BorderFactory.createEmptyBorder(24, 24, 24, 24)));
    card.add(top, BorderLayout.NORTH);
    card.add(choicesPanel, BorderLayout.CENTER);
    card.add(feedbackArea, BorderLayout.SOUTH);

    JPanel nextRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    nextRow.add(next);
    add(card, BorderLayout.CENTER);
    add(nextRow, BorderLayout.SOUTH);
  }

  /**
   * Pauses the procedure with the checkpoint that follows a step.
   *
   * @param stepIndex the index of the step the student just finished
   * @param onContinue called when the student answers correctly and clicks Continue
   */
  public void open(int stepIndex, Runnable onContinue) {
    this.onContinue = onContinue;
    showCheckpoint.execute(stepIndex);
  }

  /** Redraws the question when a new one loads, or the feedback when an answer is checked. */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (CheckpointViewModel.QUESTION.equals(event.getPropertyName())) {
      drawQuestion();
    }
    drawFeedback();
    revalidate();
    repaint();
  }

  private void drawQuestion() {
    heading.setText("Checkpoint: " + viewModel.getStepTitle());
    prompt.setText(wrap(viewModel.getPrompt()));
    choicesPanel.removeAll();
    choiceButtons.clear();
    ButtonGroup group = new ButtonGroup();
    for (String choice : viewModel.getChoices()) {
      AbstractButton button;
      if (viewModel.isMultipleChoice()) {
        button = new JCheckBox(wrap(choice));
      } else {
        button = new JRadioButton(wrap(choice));
        group.add(button);
      }
      button.setOpaque(false);
      // A changed answer hasn't been checked yet, so the old verdict no longer applies.
      button.addItemListener(event -> {
        if (!viewModel.isCorrect()) {
          feedback.setText("");
        }
      });
      choiceButtons.add(button);
      choicesPanel.add(button);
    }
    check.setEnabled(!choiceButtons.isEmpty());
  }

  private void drawFeedback() {
    boolean correct = viewModel.isCorrect();
    message.setText(viewModel.getMessage());
    feedback.setForeground(correct ? Theme.GREEN : Theme.RED);
    String text = viewModel.getVerdict().isEmpty() ? "" : "<b>" + viewModel.getVerdict() + "</b>";
    if (!viewModel.getExplanation().isEmpty()) {
      text += " " + viewModel.getExplanation();
    }
    if (!viewModel.getTakeaway().isEmpty()) {
      text += "<br><br><b>Remember:</b> " + viewModel.getTakeaway();
    }
    feedback.setText(text.isEmpty() ? "" : wrap(text));
    next.setEnabled(correct);
    next.setBackground(correct ? Theme.GREEN : Theme.DISABLED);
    next.setForeground(correct ? Theme.ON_GREEN : Color.BLACK);
    // Once correct there is nothing left to change.
    for (AbstractButton button : choiceButtons) {
      button.setEnabled(!correct);
    }
    check.setEnabled(!correct && !choiceButtons.isEmpty());
  }

  /** The indexes of the choices the student selected. */
  private Set<Integer> selected() {
    Set<Integer> selected = new HashSet<>();
    for (int i = 0; i < choiceButtons.size(); i++) {
      if (choiceButtons.get(i).isSelected()) {
        selected.add(i);
      }
    }
    return selected;
  }

  private static String wrap(String html) {
    return "<html><div style='width: " + TEXT_WIDTH + "px'>" + html + "</div></html>";
  }
}
