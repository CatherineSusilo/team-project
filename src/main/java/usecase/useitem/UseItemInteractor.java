package usecase.useitem;

import entity.Action;
import entity.ActionKind;
import entity.Lab;
import entity.LabItem;
import entity.Operation;
import entity.Scale;
import entity.Vessel;
import java.io.UncheckedIOException;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Does something to one item: writes its label (LABEL), covers it (COVER) or zeroes the scale
 * (TARE). It doesn't judge whether the label is right; the step checks that (A2: CheckStep
 * with labelMatches).
 */
public class UseItemInteractor implements UseItemInputBoundary {
  private final LabDataAccessInterface dataAccess;
  private final UseItemOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab (and its bench) is kept
   * @param presenter receives the result
   */
  public UseItemInteractor(LabDataAccessInterface dataAccess,
      UseItemOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(UseItemInputData in) {
    // each operation finds the item it needs (a container for LABEL / COVER, the scale for
    // TARE), so one operation's lookup never blocks another
    Lab lab = dataAccess.getLab();
    if (in.getOperation() == Operation.LABEL) {
      label(lab, in);
    } else if (in.getOperation() == Operation.COVER) {
      cover(lab, in);
    } else if (in.getOperation() == Operation.TARE) {
      tare(lab, in);
    } else {
      // STIR, ADD_PAPER, ... get handled with their tickets
      presenter.prepareFailView("That doesn't work yet.");
    }
  }

  // LABEL (A2, A3): write the text on a container's label.
  private void label(Lab lab, UseItemInputData in) {
    // find the item; labels only go on containers
    Vessel vessel = vesselOf(lab, in.getItemId());
    if (vessel == null) {
      presenter.prepareFailView("Pick a container.");
      return;
    }
    // an empty label isn't a label
    if (in.getText() == null || in.getText().isBlank()) {
      presenter.prepareFailView("Write something on the label.");
      return;
    }
    dataAccess.pushSnapshot();                      // before changing anything, so Undo can go back
    vessel.setLabel(in.getText().trim());           // " A " is stored as "A"
    finish(lab, in);
  }

  // COVER (A3): put the inverted beaker on top of a container, e.g. the ether cylinder.
  private void cover(Lab lab, UseItemInputData in) {
    Vessel vessel = vesselOf(lab, in.getItemId());
    if (vessel == null) {
      presenter.prepareFailView("Pick a container.");   // only containers get covered
      return;
    }
    dataAccess.pushSnapshot();
    vessel.cover();
    finish(lab, in);
  }

  // TARE (A4): zero the scale with what's on it, so the scoops of unknown are all it shows.
  private void tare(Lab lab, UseItemInputData in) {
    LabItem item = lab.getBench().getItem(in.getItemId());
    if (!(item instanceof Scale)) {
      presenter.prepareFailView("Use the scale's Tare button.");   // only the scale tares
      return;
    }
    dataAccess.pushSnapshot();
    ((Scale) item).tare(lab.getBench());           // the scale's display shows 0.000 now
    finish(lab, in);
  }

  // After a change: the steps and mistake rules see the action, the lab is saved, and the
  // screen gets the whole lab (the label tape, the cover, the scale's number, the step).
  private void finish(Lab lab, UseItemInputData in) {
    String text = "";
    if (in.getText() != null) {
      text = in.getText();
    }
    lab.record(new Action(ActionKind.USE, in.getItemId(), null, 0, in.getOperation(), text));
    presenter.prepareSuccessView(new UseItemOutputData(LabStateMapper.from(lab)));
    try {
      dataAccess.saveLab(lab);
    } catch (UncheckedIOException e) {
      // the change already happened; only writing the save file failed, so don't redo it
      presenter.prepareFailView("Done, but your progress couldn't be saved.");
    }
  }

  // The container with this id on the bench, or null if it isn't one.
  private static Vessel vesselOf(Lab lab, String id) {
    LabItem item = lab.getBench().getItem(id);
    if (item instanceof Vessel) {
      return (Vessel) item;
    }
    return null;
  }
}
