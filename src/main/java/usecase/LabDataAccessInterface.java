package usecase;

import entity.Lab;

/**
 * Loads and saves the student's lab, including their progress.
 *
 * <p>An implementation that writes to disk may throw {@link java.io.UncheckedIOException} from
 * any method that changes the lab or its undo history. The change has already been made when it
 * throws: show the student the new state with a "couldn't save" warning, and don't repeat the
 * action, or an Undo or Prev would happen twice.
 */
public interface LabDataAccessInterface {

  /**
   * Returns the student's lab.
   *
   * @return the lab the student is working on
   */
  Lab getLab();

  /**
   * Saves the lab, including how far the student has got. Every Continue button in a pre-lab
   * must call this before moving the student to the next scene.
   *
   * @param lab the lab to save
   * @throws java.io.UncheckedIOException if writing fails; the lab is still the one in memory
   */
  void saveLab(Lab lab);

  /**
   * Stores the lab as it is now, as an undo point. Call it before changing anything.
   *
   * @throws java.io.UncheckedIOException if writing fails (only on the first undo point, which
   *     also marks step 0); the undo point may not be stored
   */
  void pushSnapshot();

  /**
   * Undo: restores the last snapshot taken during the current step and forgets it.
   *
   * @return false if there is nothing to undo in this step
   * @throws java.io.UncheckedIOException if writing fails; the undo has already happened
   */
  boolean popSnapshot();

  /**
   * Marks the lab as it is now as the start of a step. Undo never goes back past it.
   *
   * @param index the step that starts here
   * @throws java.io.UncheckedIOException if writing fails; the marker is still stored
   */
  void markStepStart(int index);

  /**
   * Prev: restores the lab as it was when the step started and forgets everything after that.
   *
   * @param index the step to go back to
   * @return false if that step's start was never marked
   * @throws java.io.UncheckedIOException if writing fails; the rewind has already happened
   */
  boolean rewindToStepStart(int index);
}
