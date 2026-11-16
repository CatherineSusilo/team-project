package usecase.answercheckpoint;

import entity.Checkpoint;
import java.util.Optional;
import usecase.CheckpointDataAccessInterface;

/**
 * Checks a checkpoint answer. Only a fully correct answer reveals the explanation and takeaway.
 */
public class AnswerCheckpointInteractor implements AnswerCheckpointInputBoundary {
  private final CheckpointDataAccessInterface dataAccess;
  private final AnswerCheckpointOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the checkpoints are stored
   * @param presenter receives the result
   */
  public AnswerCheckpointInteractor(CheckpointDataAccessInterface dataAccess,
      AnswerCheckpointOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(AnswerCheckpointInputData in) {
    Optional<Checkpoint> found = dataAccess.getCheckpoint(in.getStepIndex());
    if (found.isEmpty()) {
      presenter.prepareFailView("There is no checkpoint after step " + in.getStepIndex() + ".");
      return;
    }
    if (in.getSelected().isEmpty()) {
      presenter.prepareFailView("Pick an answer first.");
      return;
    }
    Checkpoint checkpoint = found.get();
    if (checkpoint.getQuestion().isCorrect(in.getSelected())) {
      presenter.prepareSuccessView(new AnswerCheckpointOutputData(true,
          checkpoint.getQuestion().getExplanation(), checkpoint.getTakeaway()));
    } else {
      presenter.prepareSuccessView(new AnswerCheckpointOutputData(false, "", ""));
    }
  }
}
