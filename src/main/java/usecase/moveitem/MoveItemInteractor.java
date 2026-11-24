package usecase.moveitem;

import entity.Action;
import entity.Bench;
import entity.ItemType;
import entity.Lab;
import entity.LabItem;
import entity.Scale;
import entity.Vec2;
import entity.Vessel;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Moves an item to where the student let go of it.
 */
public class MoveItemInteractor implements MoveItemInputBoundary {

  private final LabDataAccessInterface dataAccess;
  private final MoveItemOutputBoundary presenter;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab is kept
   * @param presenter receives the result
   */
  public MoveItemInteractor(LabDataAccessInterface dataAccess,
      MoveItemOutputBoundary presenter) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
  }

  @Override
  public void execute(MoveItemInputData in) {
    Lab lab = dataAccess.getLab();
    LabItem item = lab.getBench().getItem(in.getItemId());
    if (item == null) {
      presenter.prepareFailView("That item isn't on the bench.");
      return;
    }
    item.setPosition(new Vec2(in.getX(), in.getY()));
    String nearId = null;
    for (LabItem other : lab.getBench().getItems()) {
      if (other != item && item.isNear(other)) {
        nearId = other.getId();
        break;
      }
    }
    updatePlacements(lab.getBench(), item);
    lab.record(Action.move(item.getId(), nearId));
    dataAccess.saveLab(lab);
    presenter.prepareSuccessView(new MoveItemOutputData(LabStateMapper.from(lab)));
  }

  // A4: a tube dropped by a beaker goes in it, and a vessel dropped by the scale goes on it.
  // Moving an item away takes it off the scale / out of the beaker again.
  private static void updatePlacements(Bench bench, LabItem item) {
    for (LabItem other : bench.getItems()) {
      if (other instanceof Scale && item.getId().equals(((Scale) other).getPlacedId())
          && !item.isNear(other)) {
        ((Scale) other).remove();
      }
      if (other instanceof Vessel && item.getId().equals(((Vessel) other).getHoldingId())
          && !item.isNear(other)) {
        ((Vessel) other).releaseHeld();
      }
    }
    if (!(item instanceof Vessel)) {
      return;
    }
    for (LabItem other : bench.getItems()) {
      if (item.getType() == ItemType.CONICAL_TUBE && other.getType() == ItemType.BEAKER
          && item.isNear(other)) {
        ((Vessel) other).hold(item.getId()); // the beaker comes first: tube in beaker
        return;
      }
    }
    for (LabItem other : bench.getItems()) {
      if (other instanceof Scale && item.isNear(other)) {
        ((Scale) other).place(item.getId());
        return;
      }
    }
  }
}
