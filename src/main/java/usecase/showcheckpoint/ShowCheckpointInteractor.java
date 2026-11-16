package usecase.showcheckpoint;

import entity.Checkpoint;
import entity.QuizQuestion;
import java.util.Optional;
import usecase.CheckpointDataAccessInterface;

/**
 * Looks up the checkpoint that follows a step and hands its question to the presenter.
 */
public class ShowCheckpointInteractor implements ShowCheckpointInputBoundary {
  private final CheckpointDataAccessInterface dataAccess;
  private final ShowCheckpointOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the checkpoints are stored
   * @param presenter receives the question
   */
  public ShowCheckpointInteractor(CheckpointDataAccessInterface dataAccess,
      ShowCheckpointOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(ShowCheckpointInputData in) {
    Optional<Checkpoint> found = dataAccess.getCheckpoint(in.getStepIndex());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no checkpoint after step " + in.getStepIndex() + ".");
      return;
    }
    Checkpoint checkpoint = found.get();
    QuizQuestion question = checkpoint.getQuestion();
    presenter.prepareSuccessView(new ShowCheckpointOutputData(checkpoint.getStepIndex(),
        checkpoint.getStepTitle(), question.getPrompt(), question.getChoices(),
        question.getType() == QuizQuestion.Type.MULTIPLE));
  }
}
