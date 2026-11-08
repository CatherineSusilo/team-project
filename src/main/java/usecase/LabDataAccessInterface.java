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
}
