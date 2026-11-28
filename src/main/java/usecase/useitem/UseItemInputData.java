package usecase.useitem;

import entity.Operation;

/**
 * What the student did to one item, e.g. wrote "A" on the conical tube's label.
 */
public final class UseItemInputData {
  private final String itemId;
  private final Operation operation;
  private final String targetId;
  private final String text;

  /**
   * Creates the input.
   *
   * @param itemId the item being used, e.g. "conical-1"
   * @param operation what is done to it, e.g. LABEL
   * @param text the label text for LABEL, otherwise ""
   */
  public UseItemInputData(String itemId, Operation operation, String text) {
    this(itemId, operation, null, text);
  }

  /**
   * Creates the input for an operation that also needs a second item.
   *
   * @param itemId the item being used, e.g. "melting_point_app-1"
   * @param operation what is done with it, e.g. START
   * @param targetId the other item, e.g. the watch glass the sample comes from (START)
   * @param text the label text for LABEL, otherwise ""
   */
  public UseItemInputData(String itemId, Operation operation, String targetId, String text) {
    this.itemId = itemId;
    this.operation = operation;
    this.targetId = targetId;
    this.text = text;
  }

  public String getItemId() {
    return itemId;
  }

  public Operation getOperation() {
    return operation;
  }

  public String getTargetId() {
    return targetId;
  }

  public String getText() {
    return text;
  }
}
