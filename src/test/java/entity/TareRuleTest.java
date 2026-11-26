package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class TareRuleTest {

  private final Lab lab = new Lab(
      List.of(new CheckStep(0, "A4", "Weigh", "Hint", List.of(), false, l -> false, a -> true)),
      new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  private final Scale scale = new Scale("scale-1", new Vec2(0, 0));
  private final Vessel beaker = new Vessel("beaker-1", ItemType.BEAKER, new Vec2(0, 0), 50, 30);
  private final Vessel tube = new Vessel("conical-1", ItemType.CONICAL_TUBE, new Vec2(0, 0), 15,
      6);

  TareRuleTest() {
    lab.getBench().addItem(scale, new Vec2(1, 1));
    lab.getBench().addItem(beaker, new Vec2(1, 1));
    lab.getBench().addItem(tube, new Vec2(1, 1));
    beaker.hold("conical-1");
    scale.place("beaker-1");
  }

  private String tare() {
    return new TareRule().check(lab,
        new Action(ActionKind.USE, "scale-1", null, 0, Operation.TARE, ""));
  }

  @Test
  void tarringTheEmptyTubeIsFine() {
    assertNull(tare());
  }

  @Test
  void tarringWithTheUnknownAlreadyInTheTubeIsFlagged() {
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.2);

    assertEquals("Tare the scale with the empty tube first.", tare());
  }

  @Test
  void anEmptyScaleIsFine() {
    scale.remove();

    assertNull(tare());
  }

  @Test
  void onlyTarringIsChecked() {
    new UnknownSample('A', ApiType.ASPIRIN).addTo(tube.getMixture(), 0.2);
    Action label = new Action(ActionKind.USE, "scale-1", null, 0, Operation.LABEL, "x");

    assertNull(new TareRule().check(lab, label));
  }
}
