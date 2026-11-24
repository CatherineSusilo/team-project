package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Lab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryLabSnapshotTest {

  /** A lab with one number in it, so a restored state can be told apart from another. */
  static final class CountingLab extends Lab {
    private int count;

    CountingLab(int count) {
      this.count = count;
    }

    int getCount() {
      return count;
    }

    void setCount(int count) {
      this.count = count;
    }
  }

  private InMemoryLabDataAccessObject dao;

  @BeforeEach
  void setUp() {
    dao = new InMemoryLabDataAccessObject();
    dao.saveLab(new CountingLab(0));
  }

  private int count() {
    return ((CountingLab) dao.getLab()).getCount();
  }

  private void change(int newCount) {
    ((CountingLab) dao.getLab()).setCount(newCount);
  }

  @Test
  void nothingToUndoAtTheStart() {
    assertFalse(dao.popSnapshot());
    assertEquals(0, count());
  }

  @Test
  void pushPushPopRestoresTheEarlierState() {
    dao.pushSnapshot();
    change(1);
    dao.pushSnapshot();
    change(2);

    assertTrue(dao.popSnapshot());
    assertEquals(1, count());
    assertTrue(dao.popSnapshot());
    assertEquals(0, count());
    assertFalse(dao.popSnapshot());
    assertEquals(0, count());
  }

  @Test
  void popAtTheStepStartReturnsFalseAndChangesNothing() {
    change(5);
    dao.markStepStart(1);

    assertFalse(dao.popSnapshot());
    assertEquals(5, count());
  }

  @Test
  void undoDoesNotCrossTheStepStartMarker() {
    dao.pushSnapshot();
    change(1);
    dao.markStepStart(1);
    dao.pushSnapshot();
    change(2);

    assertTrue(dao.popSnapshot());
    assertEquals(1, count());
    assertFalse(dao.popSnapshot());
    assertEquals(1, count());
  }

  @Test
  void rewindRestoresTheStepStartAndDropsLaterSnapshots() {
    dao.markStepStart(0);
    change(1);
    dao.pushSnapshot();
    change(2);
    dao.markStepStart(1);
    change(3);
    dao.pushSnapshot();
    change(4);

    assertTrue(dao.rewindToStepStart(0));

    assertEquals(0, count());
    assertFalse(dao.popSnapshot());
    assertFalse(dao.rewindToStepStart(1));
  }

  @Test
  void rewindToAStepThatWasNeverMarkedReturnsFalse() {
    dao.markStepStart(0);
    change(1);

    assertFalse(dao.rewindToStepStart(3));
    assertEquals(1, count());
  }

  @Test
  void firstSnapshotMarksStepZeroSoPrevCanReturnToIt() {
    dao.pushSnapshot();
    change(1);
    dao.markStepStart(1);
    change(2);

    assertTrue(dao.rewindToStepStart(0));
    assertEquals(0, count());
  }

  @Test
  void restoredLabIsAnIndependentCopy() {
    dao.pushSnapshot();
    change(1);
    assertTrue(dao.popSnapshot());

    change(9);
    dao.pushSnapshot();
    change(10);
    assertTrue(dao.popSnapshot());
    assertEquals(9, count());
  }

  @Test
  void markingAStepDropsUndoPointsItCanNoLongerReach() {
    dao.pushSnapshot();
    change(1);
    dao.pushSnapshot();
    change(2);
    dao.markStepStart(1);

    assertEquals(2, dao.getSnapshots().size());
    assertFalse(dao.popSnapshot());
    assertTrue(dao.rewindToStepStart(0));
    assertEquals(0, count());
  }

  @Test
  void historyCopiedToAnotherDaoStillUndoes() {
    dao.pushSnapshot();
    change(1);
    dao.pushSnapshot();
    change(2);

    InMemoryLabDataAccessObject other = new InMemoryLabDataAccessObject();
    other.restoreHistory(dao.getLab(), dao.getSnapshots());

    assertTrue(other.popSnapshot());
    assertEquals(1, ((CountingLab) other.getLab()).getCount());
    assertTrue(other.popSnapshot());
    assertEquals(0, ((CountingLab) other.getLab()).getCount());
    assertFalse(other.popSnapshot());
  }
}
