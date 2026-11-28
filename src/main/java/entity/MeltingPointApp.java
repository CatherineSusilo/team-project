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
   * Melts the sample and reads off the range.
   *
   * @param sample the solid loaded into the apparatus, e.g. the watch glass's contents
   * @return the melting range in °C, or null if there is no drug in the sample
   */
  public Range measure(Mixture sample) {
    for (ApiType api : ApiType.values()) {
      Substance drug = UnknownSample.drugOf(api);
      if (sample.amountOf(drug) > 0) {
        Range pure = drug.getMeltingRange();
        boolean impure = sample.amountOf(Substances.VANILLIN) > 0 || sample.totalVolume() > 0;
        if (!impure) {
          return pure;
        }
        return new Range(pure.getLo() - IMPURE_LO_DROP, pure.getHi() - IMPURE_HI_DROP);
      }
    }
    return null;
  }
}
