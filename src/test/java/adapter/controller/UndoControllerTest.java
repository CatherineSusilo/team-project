package adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import usecase.undo.UndoInputBoundary;

class UndoControllerTest {

  @Test
  void eachCallSendsOneUndoRequest() {
    int[] calls = {0};
    UndoInputBoundary interactor = in -> calls[0]++;
    UndoController controller = new UndoController(interactor);

    controller.execute();
    controller.execute();

    assertEquals(2, calls[0]);
  }
}
