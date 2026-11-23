package usecase.useitem;

import entity.Operation;

/**
 * What the student did to one item, e.g. wrote "A" on the conical tube's label.
 */
public final class UseItemInputData {
  private final String itemId;
  private final Operation operation;
  private final String text;

  /**
   * Creates the input.
   *
   * @param itemId the item being used, e.g. "conical-1"
   * @param operation what is done to it, e.g. LABEL
   * @param text the label text for LABEL, otherwise ""
   */
  public UseItemInputData(String itemId, Operation operation, String text) {
    this.itemId = itemId;
    this.operation = operation;
    this.text = text;
  }

  public String getItemId() {
    return itemId;
  }

  public Operation getOperation() {
    return operation;
  }

  public String getText() {
    return text;
  }
}
