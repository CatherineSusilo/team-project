package view;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Map;
import javax.swing.Icon;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

/**
 * Turns chemistry typed as plain text, like {@code 2H2 + O2 -> 2H2O}, into LaTeX, and draws
 * LaTeX as an icon. Lets students write equations without knowing LaTeX.
 */
final class ChemistryLatex {
  /** Plain-text arrows, longest first so {@code <=>} isn't read as {@code <=} then {@code >}. */
  private static final String[][] ARROWS = {
      {"<=>", "\\rightleftharpoons"}, {"<->", "\\rightleftharpoons"},
      {"->", "\\rightarrow"}, {"<-", "\\leftarrow"}};
  /** Symbols the equation buttons insert, so the typed text reads like the result. */
  private static final Map<Character, String> SYMBOLS = Map.of(
      '→', "\\rightarrow", '⇌', "\\rightleftharpoons", '↑', "\\uparrow",
      '↓', "\\downarrow", 'Δ', "\\Delta", '°', "^{\\circ}", '·', "\\cdot");
  private static final String ESCAPED = "%#&$";

  private ChemistryLatex() {
  }

  /**
   * Converts plain-text chemistry to LaTeX. Digits after an element or ")" become subscripts,
   * a "+" or "-" right after a formula (e.g. {@code Na+}) or a number and sign after "^"
   * (e.g. {@code Cu^2+}) becomes a charge, and {@code ->} / {@code <=>} become arrows.
   * Everything else is kept, so LaTeX like {@code x^a} or {@code H_{2}} still works.
   *
   * <p>A charge with a number needs "^": {@code Fe2+} reads as Fe&#8322;&#8314;, because it
   * looks exactly like {@code NH4+} (NH&#8324;&#8314;), where the 4 must be a subscript.
   *
   * @param plain what the student typed
   * @return the LaTeX for it
   */
  static String toLatex(String plain) {
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < plain.length()) {
      String[] arrow = arrowAt(plain, i);
      if (arrow != null) {
        out.append(' ').append(arrow[1]).append(' ');
        i += arrow[0].length();
        continue;
      }
      char c = plain.charAt(i);
      char prev = i > 0 ? plain.charAt(i - 1) : ' ';
      char next = i + 1 < plain.length() ? plain.charAt(i + 1) : ' ';
      int chargeEnd = c == '^' ? chargeEnd(plain, i + 1) : i;
      if (Character.isDigit(c) && (Character.isLetter(prev) || prev == ')' || prev == ']')) {
        int end = digitsEnd(plain, i);
        out.append("_{").append(plain, i, end).append('}');
        i = end;
      } else if (chargeEnd > i + 1) {
        out.append("^{").append(plain, i + 1, chargeEnd).append('}');
        i = chargeEnd;
      } else if ((c == '+' || c == '-') && (Character.isLetterOrDigit(prev) || prev == ')')
          && (next == ' ' || next == '(' || i + 1 == plain.length())) {
        out.append("^{").append(c).append('}');
        i++;
      } else {
        if (ESCAPED.indexOf(c) >= 0) {
          out.append('\\');
        }
        out.append(SYMBOLS.getOrDefault(c, String.valueOf(c)));
        i++;
      }
    }
    return "\\mathrm{" + out + "}";
  }

  /**
   * Draws LaTeX as an icon for the preview and the notebook.
   *
   * @param latex the LaTeX to draw
   * @param size the text size in points
   * @return the drawn equation
   * @throws RuntimeException if the LaTeX can't be read
   */
  static EquationIcon render(String latex, float size) {
    return new EquationIcon(latex,
        new TeXFormula(latex).createTeXIcon(TeXConstants.STYLE_DISPLAY, size));
  }

  /** A drawn equation that remembers its LaTeX, so a saved notebook can keep it. */
  static final class EquationIcon implements Icon {
    private final String latex;
    private final Icon drawn;

    private EquationIcon(String latex, Icon drawn) {
      this.latex = latex;
      this.drawn = drawn;
    }

    String getLatex() {
      return latex;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
      drawn.paintIcon(component, graphics, x, y);
    }

    @Override
    public int getIconWidth() {
      return drawn.getIconWidth();
    }

    @Override
    public int getIconHeight() {
      return drawn.getIconHeight();
    }
  }

  /** The {plain, LaTeX} arrow starting at index, or null if none does. */
  private static String[] arrowAt(String text, int index) {
    for (String[] arrow : ARROWS) {
      if (text.startsWith(arrow[0], index)) {
        return arrow;
      }
    }
    return null;
  }

  /** Where a charge like "2+", "-" or "3" starting at start ends; start if there is none. */
  private static int chargeEnd(String text, int start) {
    int end = digitsEnd(text, start);
    if (end < text.length() && (text.charAt(end) == '+' || text.charAt(end) == '-')) {
      end++;
    }
    return end;
  }

  private static int digitsEnd(String text, int start) {
    int end = start;
    while (end < text.length() && Character.isDigit(text.charAt(end))) {
      end++;
    }
    return end;
  }
}
