package usecase;

import entity.Lab;

/**
 * Loads and saves the student's lab, including their progress.
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
   */
  void saveLab(Lab lab);

  /**
   * Stores the lab as it is now, as an undo point. Call it before changing anything.
   */
  void pushSnapshot();

  /**
   * Undo: restores the last snapshot taken during the current step and forgets it.
   *
   * @return false if there is nothing to undo in this step
   */
  boolean popSnapshot();

  /**
   * Marks the lab as it is now as the start of a step. Undo never goes back past it.
   *
   * @param index the step that starts here
   */
  void markStepStart(int index);

  /**
   * Prev: restores the lab as it was when the step started and forgets everything after that.
   *
   * @param index the step to go back to
   * @return false if that step's start was never marked
   */
  boolean rewindToStepStart(int index);
}
