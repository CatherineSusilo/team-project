package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The tray: what the student may take for this step. Each tile is dragged onto the bench.
 */
class EquipmentPanel extends JPanel {

  private static final int TILE_HEIGHT = 84;
  private static final int SPRITE_SIZE = 56;

  private final DragDropManager manager;
  private final JPanel tiles = new JPanel();
  private List<String> shown = List.of();

  /**
   * Builds the tray.
   *
   * @param manager told when a tile is dragged or let go
   */
  EquipmentPanel(DragDropManager manager) {
    super(new BorderLayout());
    this.manager = manager;
    setPreferredSize(new Dimension(210, 0));
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.DISABLED));
    JLabel heading = new JLabel("Equipment");
    heading.setFont(Theme.bold(Theme.HEADER_SIZE));
    heading.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
    add(heading, BorderLayout.NORTH);
    tiles.setLayout(new BoxLayout(tiles, BoxLayout.Y_AXIS));
    tiles.setOpaque(false);
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
      setMaximumSize(new Dimension(Integer.MAX_VALUE, TILE_HEIGHT));
      setPreferredSize(new Dimension(190, TILE_HEIGHT));
      setAlignmentX(Component.LEFT_ALIGNMENT);
      setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
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
      Image sprite = SpriteCatalog.spriteOf(templateId);
      if (sprite != null) {
        double scale = Math.min(1.0, (double) SPRITE_SIZE
            / Math.max(sprite.getWidth(null), sprite.getHeight(null)));
        int width = (int) Math.round(sprite.getWidth(null) * scale);
        int height = (int) Math.round(sprite.getHeight(null) * scale);
        g.drawImage(sprite, 12 + (SPRITE_SIZE - width) / 2,
            (getHeight() - height) / 2, width, height, null);
      } else {
        g.setColor(Theme.DISABLED);
        g.fillRoundRect(12, (getHeight() - SPRITE_SIZE) / 2, SPRITE_SIZE, SPRITE_SIZE, 10, 10);
      }
      g.setColor(Color.DARK_GRAY);
      g.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE));
      g.drawString(SpriteCatalog.nameOf(templateId), 12 + SPRITE_SIZE + 10,
          getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 2);
      g.setColor(Theme.DISABLED);
      g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
      g.dispose();
    }
  }
}
