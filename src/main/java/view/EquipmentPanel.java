package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The tray, drawn as the dark counter under the fume hood: what the student may take for this step.
 * Each tile is dragged up onto the bench.
 */
class EquipmentPanel extends JPanel {

  private static final Color COUNTER = new Color(0x3C3C3C);
  private static final int TILE_WIDTH = 104;
  private static final int TILE_HEIGHT = 96;
  private static final int SPRITE_SIZE = 52;

  private final DragDropManager manager;
  private final JPanel tiles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
  private final JLabel empty = new JLabel("Nothing to pick up for this step.");
  private List<String> shown = List.of();

  /**
   * Builds the tray.
   *
   * @param manager told when a tile is dragged or let go
   */
  EquipmentPanel(DragDropManager manager) {
    super(new BorderLayout());
    this.manager = manager;
    setPreferredSize(new Dimension(0, 108));
    setBackground(COUNTER);
    setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    tiles.setOpaque(false);
    empty.setForeground(new Color(0xB8C0BF));
    empty.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL));
    tiles.add(empty);
    add(tiles, BorderLayout.CENTER);
  }

  /**
   * Shows the tray for the current step.
   *
   * @param templateIds the template ids on offer, in order
   */
  void showTray(List<String> templateIds) {
    if (templateIds.equals(shown)) {
      return;
    }
    shown = List.copyOf(templateIds);
    tiles.removeAll();
    if (shown.isEmpty()) {
      tiles.add(empty);
    }
    for (String templateId : shown) {
      tiles.add(new Tile(templateId));
    }
    tiles.revalidate();
    tiles.repaint();
  }

  /**
   * The template ids the tray is showing.
   *
   * @return the ids, in order
   */
  List<String> getShown() {
    return shown;
  }

  /** One draggable tile. */
  private final class Tile extends JPanel {

    private final String templateId;

    Tile(String templateId) {
      this.templateId = templateId;
      setOpaque(false);
      setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      MouseAdapter mouse = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent event) {
          manager.startTrayDrag(templateId);
        }

        @Override
        public void mouseDragged(MouseEvent event) {
          manager.trayDragged(Tile.this, event.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent event) {
          manager.trayReleased(Tile.this, event.getPoint());
        }
      };
      addMouseListener(mouse);
      addMouseMotionListener(mouse);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setColor(new Color(0x4A4A4A));
      g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
      Image sprite = SpriteCatalog.spriteOf(templateId);
      if (sprite != null) {
        double scale = Math.min(1.0, (double) SPRITE_SIZE
            / Math.max(sprite.getWidth(null), sprite.getHeight(null)));
        int width = (int) Math.round(sprite.getWidth(null) * scale);
        int height = (int) Math.round(sprite.getHeight(null) * scale);
        g.drawImage(sprite, (getWidth() - width) / 2, 8 + (SPRITE_SIZE - height) / 2, width,
            height, null);
      } else {
        g.setColor(new Color(0x6A6A6A));
        g.fillRoundRect((getWidth() - SPRITE_SIZE) / 2, 8, SPRITE_SIZE, SPRITE_SIZE, 10, 10);
      }
      g.setColor(Color.WHITE);
      g.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE_SMALL));
      String name = SpriteCatalog.nameOf(templateId);
      g.drawString(name, (getWidth() - g.getFontMetrics().stringWidth(name)) / 2,
          getHeight() - 10);
      g.dispose();
    }
  }
}
