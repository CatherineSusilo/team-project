package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class LabelRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A2", "Label", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final Vessel conical = new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0),
      15, 6);
  private final Vessel testTube = new Vessel("test-1", ItemType.TEST_TUBE, new Vec2(0, 0), 15, 8);

  LabelRuleTest() {
    lab.getBench().addItem(conical, new Vec2(1, 1));
    lab.getBench().addItem(testTube, new Vec2(1, 1));
  }

  private String label(String itemId) {
    return new LabelRule().check(lab,
        new Action(ActionKind.USE, itemId, null, 0, Operation.LABEL, ""));
  }

  @Test
  void theBatchLetterOnTheConicalTubeIsFine() {
    conical.setLabel("A");

    assertNull(label("conical-1"));
  }

  @Test
  void capitalsAndSpacesDoNotMatter() {
    conical.setLabel(" a ");

    assertNull(label("conical-1"));
  }

  @Test
  void anotherLabelOnTheConicalTubeIsFlagged() {
    conical.setLabel("B");

    assertEquals("Label it with your sample ID.", label("conical-1"));
  }

  @Test
  void otherContainersAreNotJudged() {
    testTube.setLabel("anything");

    assertNull(label("test-1"));
  }

  @Test
  void onlyLabellingIsChecked() {
    conical.setLabel("B");
    Action cover = new Action(ActionKind.USE, "conical-1", null, 0, Operation.COVER, "");

    assertNull(new LabelRule().check(lab, cover));
  }
}
