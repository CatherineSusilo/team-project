package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RecoveryCalculatorTest {

  private final RecoveryCalculator calculator = new RecoveryCalculator();

  @Test
  void fullRecoveryIsOneHundred() {
    assertEquals(100.0, calculator.percentRecovery(0.200, 0.200), 1e-9);
  }

  @Test
  void typicalRecovery() {
    assertEquals(80.0, calculator.percentRecovery(0.200, 0.160), 1e-9);
  }

  @Test
  void roundsToOneDecimal() {
    assertEquals(75.0, calculator.percentRecovery(0.200, 0.150), 1e-9);  // 74.99999999999999
    assertEquals(77.3, calculator.percentRecovery(0.203, 0.157), 1e-9);  // 77.339...
  }

  @Test
  void zeroEndIsZeroPercent() {
    assertEquals(0.0, calculator.percentRecovery(0.200, 0.0), 1e-9);
  }

  @Test
  void overOneHundredIsReturnedNotClamped() {
    // A wet solid weighs more than it should; the B6 warning needs to see it.
    assertEquals(125.0, calculator.percentRecovery(0.200, 0.250), 1e-9);
  }

  @Test
  void zeroStartThrows() {
    assertThrows(IllegalArgumentException.class, () -> calculator.percentRecovery(0.0, 0.1));
  }

  @Test
  void negativeStartThrows() {
    assertThrows(IllegalArgumentException.class, () -> calculator.percentRecovery(-0.2, 0.1));
  }

  @Test
  void nanStartThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> calculator.percentRecovery(Double.NaN, 0.1));
  }
}
