package usecase.spawnitem;

/**
 * Which tray item the student dropped on the bench, and where.
 */
public class SpawnItemInputData {

  private final String templateId;
  private final double horizontal;
  private final double vertical;

  /**
   * Creates the data.
   *
   * @param templateId the tray item's template id
   * @param x where it was dropped, horizontally
   * @param y where it was dropped, vertically
   */
  public SpawnItemInputData(String templateId, double x, double y) {
    this.templateId = templateId;
    this.horizontal = x;
    this.vertical = y;
  }

  public String getTemplateId() {
    return templateId;
  }

  public double getX() {
    return horizontal;
  }

  public double getY() {
    return vertical;
  }
}
