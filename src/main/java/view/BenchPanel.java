package view;

import adapter.viewmodel.ItemState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;

/**
 * The fume-hood bench. Draws every item at its place and lets the student drag them around.
 */
class BenchPanel extends JPanel {

  private static final int MAX_SIZE = 110;
  private static final int BOX_SIZE = 70;

  private final DragDropManager manager;
  private List<ItemState> items = List.of();
  private String draggedId;
  private Point dragCentre;
  private Point grabOffset = new Point();
  private String ghostTemplate;
  private Point ghostCentre;

  /**
   * Builds the bench.
   *
   * @param manager told when an item is let go
   */
  BenchPanel(DragDropManager manager) {
    this.manager = manager;
    setBackground(new Color(0xF3F6F5));
    setPreferredSize(new Dimension(560, 480));
    MouseAdapter mouse = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent event) {
        grab(event.getPoint());
      }

      @Override
      public void mouseDragged(MouseEvent event) {
        if (draggedId != null) {
          dragCentre = new Point(event.getX() + grabOffset.x, event.getY() + grabOffset.y);
          repaint();
        }
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        release();
      }
    };
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
  }

  /**
   * Shows what is on the bench.
   *
   * @param newItems the items, back to front
   */
  void showItems(List<ItemState> newItems) {
    this.items = List.copyOf(newItems);
    repaint();
  }

  /**
   * Shows a faint copy of a tray item following the mouse, or hides it.
   *
   * @param templateId the tray item being dragged, or null for none
   * @param centre where it is, or null to hide it
   */
  void showGhost(String templateId, Point centre) {
    this.ghostTemplate = templateId;
    this.ghostCentre = centre;
    repaint();
  }

  /**
   * Finds which item sits at a point.
   *
   * @param point a point on the bench
   * @return the id of the topmost item there, or null if there is none
   */
  String itemAt(Point point) {
    for (int i = items.size() - 1; i >= 0; i--) {
      ItemState item = items.get(i);
      Dimension size = sizeOf(templateOf(item));
      if (Math.abs(point.x - item.getX()) <= size.width / 2.0
          && Math.abs(point.y - item.getY()) <= size.height / 2.0) {
        return item.getId();
      }
    }
    return null;
  }

  private void grab(Point point) {
    draggedId = itemAt(point);
    if (draggedId == null) {
      return;
    }
    for (ItemState item : items) {
      if (item.getId().equals(draggedId)) {
        grabOffset = new Point((int) item.getX() - point.x, (int) item.getY() - point.y);
        dragCentre = new Point((int) item.getX(), (int) item.getY());
      }
    }
  }

  private void release() {
    if (draggedId == null) {
      return;
    }
    String id = draggedId;
    int x = Math.max(0, Math.min(getWidth(), dragCentre.x));
    int y = Math.max(0, Math.min(getHeight(), dragCentre.y));
    draggedId = null;
    dragCentre = null;
    manager.itemMoved(id, x, y);
    repaint();
  }

  private static String templateOf(ItemState item) {
    String id = item.getId();
    int dash = id.lastIndexOf('-');
    return dash < 0 ? id : id.substring(0, dash);
  }

  private static Dimension sizeOf(String templateId) {
    Image sprite = SpriteCatalog.spriteOf(templateId);
    if (sprite == null) {
      return new Dimension(BOX_SIZE, BOX_SIZE);
    }
    double scale = Math.min(1.0, (double) MAX_SIZE
        / Math.max(sprite.getWidth(null), sprite.getHeight(null)));
    return new Dimension((int) Math.round(sprite.getWidth(null) * scale),
        (int) Math.round(sprite.getHeight(null) * scale));
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g = (Graphics2D) graphics.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    for (ItemState item : items) {
      boolean moving = item.getId().equals(draggedId) && dragCentre != null;
      int x = moving ? dragCentre.x : (int) item.getX();
      int y = moving ? dragCentre.y : (int) item.getY();
      drawThing(g, templateOf(item), item.getLabel(), x, y, 1f);
    }
    if (ghostTemplate != null && ghostCentre != null) {
      drawThing(g, ghostTemplate, "", ghostCentre.x, ghostCentre.y, 0.5f);
    }
    g.dispose();
  }

  private static void drawThing(Graphics2D g, String templateId, String label, int cx, int cy,
      float alpha) {
    g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER,
        alpha));
    Dimension size = sizeOf(templateId);
    int left = cx - size.width / 2;
    int top = cy - size.height / 2;
    Image sprite = SpriteCatalog.spriteOf(templateId);
    if (sprite != null) {
      g.drawImage(sprite, left, top, size.width, size.height, null);
    } else {
      g.setColor(Color.WHITE);
      g.fillRoundRect(left, top, size.width, size.height, 12, 12);
      g.setColor(Color.GRAY);
      g.drawRoundRect(left, top, size.width, size.height, 12, 12);
    }
    g.setColor(Color.DARK_GRAY);
    Font font = Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL);
    g.setFont(font);
    FontMetrics metrics = g.getFontMetrics();
    String caption = label.isEmpty() ? SpriteCatalog.nameOf(templateId) : label;
    g.drawString(caption, cx - metrics.stringWidth(caption) / 2,
        top + size.height + metrics.getAscent() + 2);
    g.setComposite(java.awt.AlphaComposite.SrcOver);
  }
}
