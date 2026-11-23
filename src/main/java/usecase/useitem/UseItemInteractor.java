package usecase.useitem;

import entity.Operation;
import entity.Vessel;
import usecase.UseItemDataAccessInterface;

/**
 * Does something to one item: writes its label (LABEL). It doesn't judge
 * whether the label is right; the step checks that (A2: CheckStep with labelMatches).
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
    // find the item; labels only go on containers
    Vessel vessel = dataAccess.getVessel(in.getItemId());
    if (vessel == null) {
      presenter.prepareFailView("Pick a container.");
      return;
    }

    if (in.getOperation() == Operation.LABEL) {
      // an empty label isn't a label
      if (in.getText() == null || in.getText().isBlank()) {
        presenter.prepareFailView("Write something on the label.");
        return;
      }
      vessel.setLabel(in.getText().trim());           // " A " is stored as "A"
      presenter.prepareSuccessView(describe(in.getItemId(), vessel,
          "Labelled it " + vessel.getLabel() + "."));
    } else {
      // TARE, STIR, ... get handled with their tickets
      presenter.prepareFailView("That doesn't work yet.");
    }
  }

  // Copies what the screen needs out of the vessel (label tape).
  private UseItemOutputData describe(String itemId, Vessel vessel, String message) {
    return new UseItemOutputData(itemId, vessel.getLabel(), message);
  }
}
