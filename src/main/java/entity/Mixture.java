package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Everything inside one container (a tube, cylinder or beaker): its layers and its pH. It
 * Starts empty. Adding something or mixing builds new layers.
 * THerefore, the layers themselves never change.
 * Layers are listed bottom first, ex: the water layer sits under the ether layer.
 * Water and ether are measured in mL, otherwise in mmol.
 */
public class Mixture {
  /** Pipette pumps needed to fully mix the tube (lab manual: 30). */
  public static final int FULL_MIX = 30;

  private List<Layer> layers;
  private double ph;

  /**
   * Creates an empty mixture at pH 7, like a clean empty tube.
   */
  public Mixture() {
    this.layers = List.of();
    this.ph = 7;
  }

  public List<Layer> getLayers() {
    return layers;
  }

  public double getPh() {
    return ph;
  }

  void setPh(double ph) {
    this.ph = ph;
  }

  /**
   * Adds a substance. Water and ether join their layer straight away. Anything else sits
   * undissolved until the mixture is mixed.
   *
   * @param substance what to add
   * @param amount mL for water or ether, mmol otherwise
   */
  public void add(Substance substance, double amount) {
    //what is in the tube right now, before adding this substance
    double aqueousMl = volumeOf(Phase.AQUEOUS);
    double organicMl = volumeOf(Phase.ORGANIC);
    // copy because Layer.getSolids() returns an unmodifiable map, but we want to add to it
    Map<Substance, Double> solids = new HashMap<>(solids());

    if (substance == Substances.WATER) {
      aqueousMl += amount;                                   // more water → bigger water layer
    } else if (substance == Substances.ETHER) {
      organicMl += amount;                                   // more ether → bigger ether layer
    } else {
      // anything else that isn't water or ether ("not mixed in yet")
      // technically not a solid
      solids.merge(substance, amount, Double::sum);          // a solid sits there until mixed
    }
    // rebuild the layers with the new amounts, but don't change the ph
    layers = buildLayers(aqueousMl, organicMl, dissolvedIn(Phase.AQUEOUS),
        dissolvedIn(Phase.ORGANIC), solids);
  }

  /**
   * Mixes the contents with a pipette. If it is pumped at least {@value #FULL_MIX} times,
   * every substance moves into the layer it dissolves in at the current pH, and anything
   * that dissolves in neither stays solid.
   *
   * @param times how many pipette pumps
   */
  public void mix(int times) {
    if (times < FULL_MIX) {
      // not mixed enough -> just a general check.
      // Follows CA because it's a business rule and should be in entity layer, not view
      return;                                                
    }
    double aqueousMl = volumeOf(Phase.AQUEOUS);
    double organicMl = volumeOf(Phase.ORGANIC);
    // create these maps to hold what will end up in each layer after mixing
    Map<Substance, Double> aqueous = new HashMap<>();        // will end up in the water layer
    Map<Substance, Double> organic = new HashMap<>();        // will end up in the ether layer
    Map<Substance, Double> solids = new HashMap<>();         // dissolves in neither

    // Goes through every substance in the tube, and puts it in the right layer (or leaves it solid)
    for (Map.Entry<Substance, Double> entry : everything().entrySet()) {
      Substance substance = entry.getKey(); // which substance
      double mmol = entry.getValue(); // how much of it is in the tube
      if (aqueousMl > 0 && substance.isSolubleIn(Phase.AQUEOUS, ph)) {
        aqueous.merge(substance, mmol, Double::sum);         // e.g. the drug after NaOH
      } else if (organicMl > 0 && substance.isSolubleIn(Phase.ORGANIC, ph)) {
        organic.merge(substance, mmol, Double::sum);         // e.g. vanillin, or the drug at first
      } else {
        solids.merge(substance, mmol, Double::sum);          // e.g. the drug after HCl
      }
    }
    layers = buildLayers(aqueousMl, organicMl, aqueous, organic, solids);
  }

  /**
   * Takes one layer out of this mixture, like drawing it off with a pipette.
   *
   * @param i which layer, counting from the bottom (0 is the bottom layer)
   * @return a new mixture holding only that layer
   */
  public Mixture separateLayer(int i) {
    Layer taken = layers.get(i);
    List<Layer> remaining = new ArrayList<>(layers);
    remaining.remove(i);
    layers = List.copyOf(remaining);                         // this tube loses that layer

    Mixture separated = new Mixture();
    separated.layers = List.of(taken);                       // the new container gets it
    separated.ph = this.ph;                                  // same liquid, so same pH
    return separated;
  }

  // Helper Function
  // Builds the layers bottom first (water under ether). Solids sit in the bottom layer.
  private List<Layer> buildLayers(double aqueousMl, double organicMl,
      Map<Substance, Double> aqueous, Map<Substance, Double> organic,
      Map<Substance, Double> solids) {
    List<Layer> built = new ArrayList<>();
    if (aqueousMl > 0) {
      built.add(new Layer(Phase.AQUEOUS, aqueousMl, aqueous, solids));
    }
    if (organicMl > 0) {
      Map<Substance, Double> organicSolids;
      if (built.isEmpty()) {
        organicSolids = solids;            // no water layer, so ether is the bottom: powder here
      } else {
        organicSolids = Map.of();          // water is the bottom, so no powder in the ether
      }
      built.add(new Layer(Phase.ORGANIC, organicMl, organic, organicSolids));
    }
    if (built.isEmpty() && !solids.isEmpty()) {
      built.add(new Layer(Phase.SOLID, 0, Map.of(), solids)); // no liquid yet, just powder
    }
    return List.copyOf(built);
  }

  // Helper Function
  // How many mL of water or ether are in the tube (0 if none)
  private double volumeOf(Phase phase) {
    for (Layer layer : layers) {
      if (layer.getPhase() == phase) {
        return layer.getVolume();
      }
    }
    return 0;
  }

  // Helper Function
  // What is dissolved in the water layer or the ether layer right now
  private Map<Substance, Double> dissolvedIn(Phase phase) {
    for (Layer layer : layers) {
      if (layer.getPhase() == phase) {
        return layer.getDissolved();
      }
    }
    return Map.of();
  }

  // Helper Function
  // The undissolved powder. It always sits in the bottom layer (or there is none).
  private Map<Substance, Double> solids() {
    if (layers.isEmpty()) {
      return Map.of();                    // empty tube return no powder (an empty map)
    } else {
      // otherwise return the undissolved powder that sits in the bottom layer
      return layers.get(0).getSolids();
    }
  }

  // Helper Function
  // Every substance in the tube, dissolved or solid, with its amount.
  private Map<Substance, Double> everything() {
    Map<Substance, Double> all = new HashMap<>(solids());       // start with the powder
    for (Layer layer : layers) {                                 // each layer in the tube
      for (Map.Entry<Substance, Double> entry : layer.getDissolved().entrySet()) {
        all.merge(entry.getKey(), entry.getValue(), Double::sum); // add what's dissolved in it
      }
    }
    return all;
  }
}