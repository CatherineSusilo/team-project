package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * The app's shared colors and button styles, so every page looks the same.
 */
final class Theme {
  static final Color GREEN = new Color(0x2E7D32);
  static final Color ON_GREEN = Color.WHITE;
  static final Color RED = new Color(0xC62828);
  static final Color YELLOW = new Color(0xF9C846);
  static final Color DISABLED = new Color(0xE0E0E0);

  private Theme() {
  }

  /**
   * A flat button filled with one color. Opaque and borderless, so the color also shows on
   * macOS.
   *
   * @param text the button text
   * @param background the fill
   * @param foreground the text color
   * @return the button
   */
  static JButton filledButton(String text, Color background, Color foreground) {
    JButton button = new JButton(text);
    button.setBackground(background);
    button.setForeground(foreground);
    button.setOpaque(true);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return button;
  }

  /**
   * A button that looks like an underlined link.
   *
   * @param text the link text
   * @param color the text color
   * @return the button
   */
  static JButton linkButton(String text, Color color) {
    JButton link = new JButton(text);
    link.setForeground(color);
    link.setFont(link.getFont().deriveFont(
        Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
    link.setBorderPainted(false);
    link.setContentAreaFilled(false);
    link.setFocusPainted(false);
    link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return link;
  }

  /**
   * Bold copy of a font.
   *
   * @param font the font to copy
   * @param size the new size in points
   * @return the bold font
   */
  static Font bold(Font font, float size) {
    return font.deriveFont(Font.BOLD, size);
  }
}
