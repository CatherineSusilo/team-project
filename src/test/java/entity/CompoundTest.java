package entity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class CompoundTest {

  @Test
  void structureImageCantBeChangedFromOutside() {
    byte[] png = {1, 2, 3};
    Compound water = new Compound("Water", "H2O", 18.015, null, null, null, List.of(), png);

    png[0] = 9;
    water.getStructureImage()[1] = 9;

    assertArrayEquals(new byte[] {1, 2, 3}, water.getStructureImage());
  }

  @Test
  void noStructureImageStaysNull() {
    assertNull(new Compound("Water", "H2O", 18.015, null, null, null, List.of(), null)
        .getStructureImage());
  }
}
