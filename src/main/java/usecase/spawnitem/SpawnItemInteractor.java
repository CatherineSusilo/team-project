package usecase.spawnitem;

import entity.Action;
import entity.ItemFactory;
import entity.ItemTemplate;
import entity.ItemTemplates;
import entity.Lab;
import entity.LabItem;
import entity.Vec2;
import java.util.Optional;
import usecase.LabDataAccessInterface;
import usecase.LabStateMapper;

/**
 * Puts a tray item on the bench. Only what the current step's tray offers can be spawned.
 */
public class SpawnItemInteractor implements SpawnItemInputBoundary {

  private final LabDataAccessInterface dataAccess;
  private final SpawnItemOutputBoundary presenter;
  private final ItemFactory factory;

  /**
   * Creates the interactor.
   *
   * @param dataAccess where the lab is kept
   * @param presenter receives the result
   * @param factory makes the items
   */
  public SpawnItemInteractor(LabDataAccessInterface dataAccess,
      SpawnItemOutputBoundary presenter, ItemFactory factory) {
    this.dataAccess = dataAccess;
    this.presenter = presenter;
    this.factory = factory;
  }

  @Override
  public void execute(SpawnItemInputData in) {
    Lab lab = dataAccess.getLab();
    Optional<ItemTemplate> template = ItemTemplates.find(in.getTemplateId());
    if (template.isEmpty()
        || !lab.currentStep().getTrayTemplates().contains(in.getTemplateId())) {
      presenter.prepareFailView("That isn't needed for this step.");
      return;
    }
    String id = lab.getBench().nextId(in.getTemplateId());
    LabItem item = factory.create(template.get(), id);
    lab.getBench().addItem(item, new Vec2(in.getX(), in.getY()));
    lab.record(Action.spawn(id));
    dataAccess.saveLab(lab);
    presenter.prepareSuccessView(new SpawnItemOutputData(LabStateMapper.from(lab)));
  }
}
