package entity;

import java.util.Map;

/**
 * One liquid layer in a tube (the water layer or the ether layer): what is dissolved in it
 * and any solid sitting in it. Amounts are in mmol, volume in mL. 
 * A layer never changes, the mixture makes new layers when its contents change.
 */
public final class Layer {
  private final Phase phase;
  private final double volume;
  private final Map<Substance, Double> dissolved;
  private final Map<Substance, Double> solids;

  /**
   * Creates a layer.
   *
   * @param phase AQUEOUS for the water layer, ORGANIC for the ether layer
   * @param volume how many mL
   * @param dissolved what is dissolved in it, in mmol
   * @param solids undissolved solid in it, in mmol
   */
  public Layer(Phase phase, double volume, Map<Substance, Double> dissolved,
      Map<Substance, Double> solids) {
    this.phase = phase;
    this.volume = volume;
    this.dissolved = Map.copyOf(dissolved);
    this.solids = Map.copyOf(solids);
  }

  public Phase getPhase() {
    return phase;
  }

  public double getVolume() {
    return volume;
  }

  public Map<Substance, Double> getDissolved() {
    return dissolved;
  }

  public Map<Substance, Double> getSolids() {
    return solids;
  }
}