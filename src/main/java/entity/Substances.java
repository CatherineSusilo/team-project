package entity;

/**
 * The chemicals used in Lab 3 made as a constant to be used throughout the lab.
 */
public final class Substances {
  public static final Substance WATER =
      new Substance("Water", "H2O", 18.02, null, Phase.AQUEOUS);
  public static final Substance ETHER =
      new Substance("Diethyl ether", "C4H10O", 74.12, null, Phase.ORGANIC);
  public static final Substance NAOH =
      new Substance("Sodium hydroxide", "NaOH", 40.00, null, Phase.AQUEOUS);
  public static final Substance HCL =
      new Substance("Hydrochloric acid", "HCl", 36.46, null, Phase.AQUEOUS);
  public static final Substance ASPIRIN =
      new Substance("Aspirin", "C9H8O4", 180.16, 3.5, new Range(135, 136), Phase.ORGANIC);
  public static final Substance MEFENAMIC_ACID =
      new Substance("Mefenamic acid", "C15H15NO2", 241.29, 4.2, new Range(230, 231),
          Phase.ORGANIC);
  public static final Substance VANILLIN =
      new Substance("Vanillin", "C8H8O3", 152.15, new Range(81, 83), Phase.ORGANIC);

  // make a private constructor to prevent instantiation
  private Substances() {
  }
}