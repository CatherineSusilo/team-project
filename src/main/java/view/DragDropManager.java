package view;

import adapter.controller.MoveItemController;
import adapter.controller.SpawnItemController;
import java.awt.Component;
import java.awt.Point;
import javax.swing.SwingUtilities;

/**
 * Turns dragging into use case calls: dropping a tray item on the bench spawns it, and
 * letting go of an item already on the bench moves it.
 */
class DragDropManager {

  private final SpawnItemController spawn;
  private final MoveItemController move;
  private BenchPanel bench;
  private String draggingTemplate;

  /**
   * Creates the manager.
   *
   * @param spawn puts a tray item on the bench
   * @param move moves an item on the bench
   */
  DragDropManager(SpawnItemController spawn, MoveItemController move) {
    this.spawn = spawn;
    this.move = move;
  }

  /**
   * Says which bench the drops land on.
   *
   * @param benchPanel the bench
   */
  void attach(BenchPanel benchPanel) {
    this.bench = benchPanel;
  }

  /**
   * A tray tile was pressed.
   *
   * @param templateId the template the tile stands for
   */
  void startTrayDrag(String templateId) {
    draggingTemplate = templateId;
  }

  /**
   * The mouse moved while holding a tray tile.
   *
   * @param source the tile
   * @param point where the mouse is, in the tile's coordinates
   */
  void trayDragged(Component source, Point point) {
    if (draggingTemplate == null) {
      return;
    }
    Point onBench = SwingUtilities.convertPoint(source, point, bench);
    bench.showGhost(draggingTemplate, bench.contains(onBench) ? onBench : null);
  }

  /**
   * A tray tile was let go.
   *
   * @param source the tile
   * @param point where the mouse is, in the tile's coordinates
   */
  void trayReleased(Component source, Point point) {
    if (draggingTemplate == null) {
      return;
    }
    Point onBench = SwingUtilities.convertPoint(source, point, bench);
    String templateId = draggingTemplate;
    draggingTemplate = null;
    bench.showGhost(null, null);
    if (bench.contains(onBench)) {
      spawn.execute(templateId, onBench.x, onBench.y);
    }
  }

  /**
   * An item on the bench was let go.
   *
   * @param itemId the item's id
   * @param x where its centre now is, horizontally
   * @param y where its centre now is, vertically
   */
  void itemMoved(String itemId, int x, int y) {
    move.execute(itemId, x, y);
  }
}
