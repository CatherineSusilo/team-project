package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChemistryLatexTest {

  @Test
  void digitsAfterAnElementBecomeSubscriptsButCoefficientsStay() {
    assertEquals("\\mathrm{2H_{2} + O_{2}  \\rightarrow  2H_{2}O}",
        ChemistryLatex.toLatex("2H2 + O2 -> 2H2O"));
    assertEquals("\\mathrm{Ca(OH)_{2}}", ChemistryLatex.toLatex("Ca(OH)2"));
    assertEquals("\\mathrm{C_{12}H_{22}O_{11}}", ChemistryLatex.toLatex("C12H22O11"));
  }

  @Test
  void signsAfterAFormulaAreChargesAndSpacedSignsArePlusAndMinus() {
    assertEquals("\\mathrm{Na^{+} + Cl^{-}}", ChemistryLatex.toLatex("Na+ + Cl-"));
    assertEquals("\\mathrm{Cu^{2+}(aq)}", ChemistryLatex.toLatex("Cu^2+(aq)"));
    assertEquals("\\mathrm{SO_{4}^{2-}}", ChemistryLatex.toLatex("SO4^2-"));
    assertEquals("\\mathrm{H_{2}O+CO_{2}}", ChemistryLatex.toLatex("H2O+CO2"));
  }

  @Test
  void aChargeWithANumberNeedsACaret() {
    assertEquals("\\mathrm{Fe^{2+}}", ChemistryLatex.toLatex("Fe^2+"));
    assertEquals("\\mathrm{NH_{4}^{+}}", ChemistryLatex.toLatex("NH4+"));
    // Without "^", Fe2+ looks just like NH4+, so the 2 is read as a subscript.
    assertEquals("\\mathrm{Fe_{2}^{+}}", ChemistryLatex.toLatex("Fe2+"));
  }

  @Test
  void caretWithNoNumberOrSignIsLeftAsTyped() {
    assertEquals("\\mathrm{x^a}", ChemistryLatex.toLatex("x^a"));
    assertEquals("\\mathrm{x^{-}}", ChemistryLatex.toLatex("x^-"));
  }

  @Test
  void arrowsAndButtonSymbolsBecomeLatex() {
    assertTrue(ChemistryLatex.toLatex("A <=> B").contains("\\rightleftharpoons"));
    assertTrue(ChemistryLatex.toLatex("A <- B").contains("\\leftarrow"));
    assertEquals("\\mathrm{CO_{2}\\uparrow 25^{\\circ}C}", ChemistryLatex.toLatex("CO2↑ 25°C"));
  }

  @Test
  void latexTheStudentTypesIsKeptAndSpecialCharactersAreEscaped() {
    assertEquals("\\mathrm{H_{2}O}", ChemistryLatex.toLatex("H_{2}O"));
    assertEquals("\\mathrm{x^{2}}", ChemistryLatex.toLatex("x^{2}"));
    assertEquals("\\mathrm{50\\% yield}", ChemistryLatex.toLatex("50% yield"));
  }

  @Test
  void rendersAnIcon() {
    assertTrue(ChemistryLatex.render(ChemistryLatex.toLatex("2H2 + O2 -> 2H2O"), 16)
        .getIconWidth() > 0);
  }
}
