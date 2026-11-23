package usecase.moveitem;

import entity.Action;
import entity.Lab;
import entity.LabItem;
import entity.Vec2;
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
    lab.record(Action.move(item.getId(), nearId));
    dataAccess.saveLab(lab);
    presenter.prepareSuccessView(new MoveItemOutputData(LabStateMapper.from(lab)));
  }
}
