package usecase.guessunknown;

import entity.Lab;
import entity.Range;
import entity.Substance;
import entity.UnknownSample;
import java.io.UncheckedIOException;
import java.util.Locale;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Checks the student's pick of which drug the unknown is (B7), once the melting range is known.
 * A right pick is recorded and can be undone. A wrong pick isn't a mistake: it gives a hint
 * comparing the melting points, changes nothing, and the student can pick again.
 */
public class GuessUnknownInteractor implements GuessUnknownInputBoundary {
  private final LabDataAccessInterface dataAccess;
  private final GuessUnknownOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab (and its record) is kept
   * @param presenter receives the result
   */
  public GuessUnknownInteractor(LabDataAccessInterface dataAccess,
      GuessUnknownOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(GuessUnknownInputData in) {
    Lab lab = dataAccess.getLab();
    Range measured = lab.getRecord().getMeltingRange();
    if (measured == null) {
      presenter.prepareFailView("Measure the melting point first.");   // nothing to go on yet
      return;
    }
    if (!lab.getUnknown().checkGuess(in.getGuess())) {
      // e.g. "Your sample melted at 125-132 °C, but mefenamic acid melts at 230-231 °C."
      Substance picked = UnknownSample.drugOf(in.getGuess());
      presenter.prepareFailView("Your sample melted at " + LabStateMapper.meltingRange(measured)
          + ", but " + picked.getName().toLowerCase(Locale.ROOT) + " melts at "
          + LabStateMapper.meltingRange(picked.getMeltingRange()) + ". Try again.");
      return;
    }
    dataAccess.pushSnapshot();
    lab.getRecord().setGuessCorrect(true);          // B7 is done now
    presenter.prepareSuccessView(new GuessUnknownOutputData(LabStateMapper.from(lab)));
    try {
      dataAccess.saveLab(lab);
    } catch (UncheckedIOException e) {
      // the guess already counted; only writing the save file failed, so don't redo it
      presenter.prepareFailView("Done, but your progress couldn't be saved.");
    }
  }
}
