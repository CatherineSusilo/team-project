package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LabMistakeTest {

  private static CheckStep needing(int id, String templateId) {
    return new CheckStep(id, "Step " + id, "Do step " + id, "Hint " + id, List.of(), false,
        lab -> lab.getBench().countOf(templateId) > 0, action -> true);
  }

  private static Lab lab(MistakeRule... rules) {
    return new Lab(List.of(needing(0, "beaker"), needing(1, "scale")), List.of(rules),
        new Bench(), new UnknownSample('A', ApiType.ASPIRIN));
  }

  private static void put(Lab lab, String templateId) {
    String id = lab.getBench().nextId(templateId);
    lab.getBench().addItem(new TemplateItem(id, ItemTemplates.find(templateId).orElseThrow(),
        new Vec2(0, 0)), new Vec2(5, 5));
    lab.record(Action.spawn(id));
  }

  @Test
  void aRulesHintBecomesTheStandingMistake() {
    Lab lab = lab((l, action) -> "Not like that.");

    put(lab, "beaker");

    assertEquals(StepStatus.MISTAKE, lab.status());
    assertEquals("Not like that.", lab.message());
  }

  @Test
  void theFirstHintWins() {
    Lab lab = lab((l, action) -> null, (l, action) -> "First.", (l, action) -> "Second.");

    put(lab, "beaker");

    assertEquals("First.", lab.message());
  }

  @Test
  void aMistakeStaysAfterAGoodActionAndLocksNext() {
    AtomicInteger calls = new AtomicInteger();
    Lab lab = lab((l, action) -> calls.getAndIncrement() == 0 ? "Oops." : null);

    put(lab, "beaker");
    put(lab, "scale");

    assertEquals(StepStatus.MISTAKE, lab.status());
    assertEquals("Oops.", lab.message());
    assertFalse(lab.advance());
  }

  @Test
  void rulesAreNotAskedAgainWhileAMistakeStands() {
    AtomicInteger calls = new AtomicInteger();
    Lab lab = lab((l, action) -> {
      calls.incrementAndGet();
      return "Oops.";
    });

    put(lab, "beaker");
    put(lab, "scale");

    assertEquals(1, calls.get());
  }

  @Test
  void goingBackClearsTheMistake() {
    Lab lab = lab((l, action) -> action.getSourceId().startsWith("scale") ? "Oops." : null);
    put(lab, "beaker");
    lab.advance();
    put(lab, "scale");
    assertEquals(StepStatus.MISTAKE, lab.status());

    lab.goBack();

    assertEquals("Hint 0", lab.message());
  }

  @Test
  void noRulesMeansNoMistake() {
    Lab lab = lab();

    put(lab, "beaker");

    assertEquals(StepStatus.COMPLETE, lab.status());
  }

  @Test
  void rulesCanBeAttachedAgainAfterARestore() {
    Lab lab = lab();
    lab.attachRules(List.of((l, action) -> "Oops."));

    put(lab, "beaker");

    assertEquals(StepStatus.MISTAKE, lab.status());
  }
}
