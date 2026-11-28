package adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import entity.ApiType;
import org.junit.jupiter.api.Test;
import usecase.guessunknown.GuessUnknownInputBoundary;

class GuessUnknownControllerTest {

  @Test
  void turnsThePickIntoTheDrug() {
    ApiType[] sent = {null};
    GuessUnknownInputBoundary interactor = in -> sent[0] = in.getGuess();
    new GuessUnknownController(interactor).execute("MEFENAMIC_ACID");
    assertEquals(ApiType.MEFENAMIC_ACID, sent[0]);
  }
}
