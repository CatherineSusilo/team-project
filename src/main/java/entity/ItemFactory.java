package entity;

/**
 * Makes the items the student takes from the tray.
 */
public class ItemFactory {

  /**
   * Makes an item from a template.
   *
   * @param template what to make
   * @param id the new item's id
   * @return the item, at the bench's origin until it is placed
   */
  public LabItem create(ItemTemplate template, String id) {
    Vec2 origin = new Vec2(0, 0);
    switch (template.getType()) {
      case CONICAL_TUBE:
      case TEST_TUBE:
      case GRADUATED_CYLINDER:
      case BEAKER:
      case WATCH_GLASS:
        return new Vessel(id, template.getType(), origin, template.getCapacity(),
            template.getGlassMassG());
      case FILTRATION_FLASK:
        return new FiltrationFlask(id, origin, template.getCapacity(), template.getGlassMassG());
      case BUCHNER_FUNNEL:
        return new BuchnerFunnel(id, origin, template.getCapacity(), template.getGlassMassG());
      case CLAMP_STAND:
        return new ClampStand(id, origin);
      case VACUUM_TAP:
        return new VacuumTap(id, origin);
      case ICE_BATH:
        return new IceBath(id, origin);
      case PIPETTE:
        return new PasteurPipette(id, origin);
      case DROPPER:
      case SOLVENT_BOTTLE:
        return new StockBottle(id, template.getType(), origin, template.getSubstance());
      case SCALE:
        return new Scale(id, origin);
      case MELTING_POINT_APP:
        return new MeltingPointApp(id, origin);
      default:
        return new TemplateItem(id, template, origin);
    }
  }
}
