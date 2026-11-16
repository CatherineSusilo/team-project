package usecase;

import entity.Checkpoint;
import java.util.Optional;

/**
 * Loads the checkpoint questions asked between procedure steps.
 */
public interface CheckpointDataAccessInterface {

  /**
   * Finds the checkpoint that comes after a step.
   *
   * @param stepIndex the index of the step just finished
   * @return its checkpoint, or empty if that step has none
   */
  Optional<Checkpoint> getCheckpoint(int stepIndex);
}
