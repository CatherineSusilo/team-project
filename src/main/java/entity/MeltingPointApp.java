package entity;

/**
 * The melting point apparatus (B7). It melts a little of the dried solid and reports the range
 * it melted over. A pure, dry drug melts at its literature range; vanillin (B4 not done) or
 * water (B5 not done) left in the solid makes it melt lower and over a wider range.
 */
public class MeltingPointApp extends LabItem {
  // How far an impure or wet sample melts below the literature range, in °C.
  private static final double IMPURE_LO_DROP = 10;
  private static final double IMPURE_HI_DROP = 4;

  /**
   * Creates the apparatus.
   *
   * @param id a unique id, e.g. "melting_point_app-1"
   * @param position where it sits on the bench
   */
  public MeltingPointApp(String id, Vec2 position) {
    super(id, ItemType.MELTING_POINT_APP, position);
  }

  /**
   * Melts the sample and reads off the range. Only undissolved drug melts: a solution has no
   * solid to load.
   *
   * @param sample the solid loaded into the apparatus, e.g. the solid on the Büchner funnel
   * @param wet true if the solid wasn't dried under vacuum (B5)
   * @return the melting range in °C, or null if there is no solid drug in the sample
   */
  public Range measure(Mixture sample, boolean wet) {
    for (ApiType api : ApiType.values()) {
      Substance drug = UnknownSample.drugOf(api);
      if (solid(sample, drug) > 0) {
        Range pure = drug.getMeltingRange();
        // liquid still around the solid counts as wet too, e.g. straight from the test tube
        boolean impure = wet || sample.amountOf(Substances.VANILLIN) > 0
            || sample.totalVolume() > 0;
        if (!impure) {
          return pure;
        }
        return new Range(pure.getLo() - IMPURE_LO_DROP, pure.getHi() - IMPURE_HI_DROP);
      }
    }
    return null;
  }

  // How much of a substance is sitting in the sample as a solid, in grams.
  private static double solid(Mixture sample, Substance substance) {
    double grams = 0;
    for (Layer layer : sample.getLayers()) {
      grams += layer.getSolids().getOrDefault(substance, 0.0);
    }
    return grams;
  }
}
