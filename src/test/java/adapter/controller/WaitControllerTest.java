package adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import usecase.wait.WaitInputBoundary;

class WaitControllerTest {

  @Test
  void sendsTheMinutesOnce() {
    double[] sent = {0};
    int[] calls = {0};
    WaitInputBoundary interactor = in -> {
      sent[0] = in.getMinutes();
      calls[0]++;
    };
    new WaitController(interactor).execute(1);
    assertEquals(1, calls[0]);
    assertEquals(1, sent[0]);
  }
}
