package adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import entity.Operation;
import org.junit.jupiter.api.Test;
import usecase.useitem.UseItemInputData;

class UseItemControllerTest {
  private UseItemInputData sent;
  private final UseItemController controller = new UseItemController(in -> sent = in);

  @Test
  void passesTheSampleForStart() {
    controller.execute("melting_point_app-1", "START", "funnel-1", "");
    assertEquals(Operation.START, sent.getOperation());
    assertEquals("melting_point_app-1", sent.getItemId());
    assertEquals("funnel-1", sent.getTargetId());
  }

  @Test
  void labelNeedsNoSecondItem() {
    controller.execute("conical_tube-1", "LABEL", "A");
    assertEquals("A", sent.getText());
    assertNull(sent.getTargetId());
  }
}
