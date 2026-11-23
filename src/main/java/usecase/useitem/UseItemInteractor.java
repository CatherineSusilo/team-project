package usecase.useitem;

import entity.Operation;
import entity.Scale;
import entity.Vessel;
import usecase.UseItemDataAccessInterface;

/**
 * Does something to one item: writes its label (LABEL), covers it (COVER) or zeroes the scale
 * (TARE). It doesn't judge whether the label is right; the step checks that (A2: CheckStep
 * with labelMatches).
 */
public class UseItemInteractor implements UseItemInputBoundary {
  private final UseItemDataAccessInterface dataAccess;
  private final UseItemOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess finds the items on the bench
   * @param presenter receives the result
   */
  public UseItemInteractor(UseItemDataAccessInterface dataAccess,
      UseItemOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(UseItemInputData in) {
    // each operation finds the item it needs (a container for LABEL / COVER, the scale for
    // TARE), so one operation's lookup never blocks another
    if (in.getOperation() == Operation.LABEL) {
      label(in);
    } else if (in.getOperation() == Operation.COVER) {
      cover(in);
    } else if (in.getOperation() == Operation.TARE) {
      tare(in);
    } else {
      // STIR, ADD_PAPER, ... get handled with their tickets
      presenter.prepareFailView("That doesn't work yet.");
    }
  }

  // LABEL (A2, A3): write the text on a container's label.
  private void label(UseItemInputData in) {
    // find the item; labels only go on containers
    Vessel vessel = dataAccess.getVessel(in.getItemId());
    if (vessel == null) {
      presenter.prepareFailView("Pick a container.");
      return;
    }
    // an empty label isn't a label
    if (in.getText() == null || in.getText().isBlank()) {
      presenter.prepareFailView("Write something on the label.");
      return;
    }
    vessel.setLabel(in.getText().trim());           // " A " is stored as "A"
    presenter.prepareSuccessView(describe(in.getItemId(), vessel,
        "Labelled it " + vessel.getLabel() + "."));
  }

  // COVER (A3): put the inverted beaker on top of a container, e.g. the ether cylinder.
  private void cover(UseItemInputData in) {
    Vessel vessel = dataAccess.getVessel(in.getItemId());
    if (vessel == null) {
      presenter.prepareFailView("Pick a container.");   // only containers get covered
      return;
    }
    vessel.cover();
    presenter.prepareSuccessView(describe(in.getItemId(), vessel, "Covered."));
  }

  // TARE (A4): zero the scale with what's on it, so the scoops of unknown are all it shows.
  private void tare(UseItemInputData in) {
    Scale scale = dataAccess.getScale(in.getItemId());
    if (scale == null) {
      presenter.prepareFailView("Use the scale's Tare button.");   // only the scale tares
      return;
    }
    scale.tare(dataAccess.getBench());
    presenter.prepareSuccessView(new UseItemOutputData(in.getItemId(), "", false,
        scale.display(dataAccess.getBench()), "Tared."));
  }

  // Copies what the screen needs out of the vessel (label tape, cover).
  private UseItemOutputData describe(String itemId, Vessel vessel, String message) {
    return new UseItemOutputData(itemId, vessel.getLabel(), vessel.isCovered(), "", message);
  }
}
