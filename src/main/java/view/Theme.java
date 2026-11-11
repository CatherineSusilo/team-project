package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JButton;

/**
 * The app's shared colors and button styles, so every page looks the same.
 */
final class Theme {
  static final Color GREEN = new Color(0x2E7D32);
  static final Color ON_GREEN = Color.WHITE;

  private Theme() {
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
