package usecase;

import entity.UnknownSample;

/**
 * Gives a student their unknown sample at the start of the lab.
 */
public interface SampleAssignerInterface {
  /** Returns a new unknown for one student. */
  UnknownSample assign();
}