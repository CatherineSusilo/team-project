package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Everything inside one container (a tube, cylinder or beaker): its layers and its pH. It
 * starts empty. Adding something or mixing builds new layers, so the layers themselves
 * never change.
 * Layers are listed bottom first, ex: the water layer sits under the ether layer.
 * Water and ether are measured in mL, solids in grams.
 */
public class Mixture {
  // Pipette pumps needed to fully mix the tube (lab manual: 30).
  public static final int FULL_MIX = 30;

  // How long the layers take to separate after mixing (lab manual A8, about 1 minute).
  // The tube itself only remembers settled or not; this is just the time advance() needs
  // before it flips to settled. No real countdown: the timer's Skip button sends it at once.
  public static final double SETTLE_MINUTES = 1;

  private List<Layer> layers;
  private double ph;
  private int mixCount;               // pipette pumps since something was last added
  private boolean settled = true;     // false right after mixing (cloudy), true once separated

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

  public int getMixCount() {
    return mixCount;
  }

  public boolean isSettled() {
    return settled;
  }

  /**
   * Lets time pass for the tube. After 1 minute (SETTLE_MINUTES) the layers separate.
   *
   * @param minutes how many lab minutes passed (the settling timer or its Skip button)
   */
  public void advance(double minutes) {
    if (minutes >= SETTLE_MINUTES) {
      settled = true;                 // two clear layers again
    }
  }

  /**
   * How much liquid is in the tube.
   *
   * @return the water and ether together, in mL
   */
  public double totalVolume() {
    double total = 0;
    for (Layer layer : layers) {
      total += layer.getVolume();
    }
    return total;
  }

  /**
   * Adds a substance. Water and ether join their layer straight away. Anything else sits
   * undissolved until the mixture is mixed.
   *
   * @param substance what to add
   * @param amount mL for water, ether or a reagent (NaOH, HCl), grams for a solid
   */
  public void add(Substance substance, double amount) {
    // what is in the tube right now, before adding this substance
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
    mixCount = 0;                                           // adding something resets the mix
  }

  /**
   * Mixes the contents with a pipette. Pumps add up until something new is added. Once there
   * have been at least {@value #FULL_MIX} pumps, every substance moves into the layer it
   * dissolves in at the current pH, and anything that dissolves in neither stays solid.
   *
   * @param times how many pipette pumps
   */
  public void mix(int times) {
    mixCount += times;                // e.g. 15 pumps in A5, then more later
    settled = false;                  // just mixed: cloudy until it separates
    if (mixCount < FULL_MIX) {
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
      double amount = entry.getValue(); // how much of it is in the tube
      if (aqueousMl > 0 && substance.isSolubleIn(Phase.AQUEOUS, ph)) {
        aqueous.merge(substance, amount, Double::sum);         // e.g. the drug after NaOH
      } else if (organicMl > 0 && substance.isSolubleIn(Phase.ORGANIC, ph)) {
        organic.merge(substance, amount, Double::sum);         // e.g. vanillin/the drug at first
      } else {
        solids.merge(substance, amount, Double::sum);          // e.g. the drug after HCl
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

  /**
   * Draws liquid up from the bottom of the tube with a pipette. Only the bottom layer is ever
   * taken, so asking for more than it holds just takes all of it (never the layer above).
   * What's dissolved goes along in the same share as the liquid; powder stays in the tube.
   *
   * @param ml how many mL to draw
   * @return a new mixture holding what was drawn, at the same pH
   */
  public Mixture drawFromBottom(double ml) {
    Mixture drawn = new Mixture();
    drawn.ph = this.ph;                                      // same liquid, so same pH
    if (totalVolume() == 0) {
      return drawn;                                          // no liquid: nothing to draw
    }
    Layer bottom = layers.get(0);
    double taken = Math.min(ml, bottom.getVolume());         // never more than the bottom layer
    double share = taken / bottom.getVolume();               // half the water = half the drug
    drawn.layers = List.of(new Layer(bottom.getPhase(), taken,
        scaled(bottom.getDissolved(), share), Map.of()));

    List<Layer> remaining = new ArrayList<>(layers);
    remaining.set(0, new Layer(bottom.getPhase(), bottom.getVolume() - taken,
        scaled(bottom.getDissolved(), 1 - share), Map.of()));
    layers = remaining;
    // rebuild so an emptied layer disappears and its powder drops to the new bottom
    layers = buildLayers(volumeOf(Phase.AQUEOUS), volumeOf(Phase.ORGANIC),
        dissolvedIn(Phase.AQUEOUS), dissolvedIn(Phase.ORGANIC), bottom.getSolids());
    if (layers.isEmpty()) {
      ph = 7;                                                // an empty tube is neutral again
    }
    return drawn;
  }

  /**
   * Pours another mixture into this one, like emptying a pipette into a tube. Same layers
   * join up (water with water, ether with ether), and the other mixture is left empty.
   *
   * @param other what is being poured in
   */
  public void pour(Mixture other) {
    // water layer: what's dissolved in our water, plus what's dissolved in theirs
    // (same substance in both, e.g. the drug: the grams add up)
    Map<Substance, Double> aqueous = new HashMap<>(dissolvedIn(Phase.AQUEOUS));
    other.dissolvedIn(Phase.AQUEOUS).forEach((s, g) -> aqueous.merge(s, g, Double::sum));

    // ether layer: same idea, our ether's contents plus theirs (e.g. vanillin)
    Map<Substance, Double> organic = new HashMap<>(dissolvedIn(Phase.ORGANIC));
    other.dissolvedIn(Phase.ORGANIC).forEach((s, g) -> organic.merge(s, g, Double::sum));

    // powder: our undissolved solid plus theirs (it sits at the bottom)
    Map<Substance, Double> solids = new HashMap<>(solids());
    other.solids().forEach((s, g) -> solids.merge(s, g, Double::sum));

    // plain water or ether never changes an acid or a base, so keep our pH unless we are
    // neutral (or empty), then take theirs (e.g. the basic water layer into the test tube)
    if (layers.isEmpty() || ph == 7) {
      ph = other.ph;
    }

    // how much liquid there is now: our water + their water, our ether + their ether
    double aqueousMl = volumeOf(Phase.AQUEOUS) + other.volumeOf(Phase.AQUEOUS);
    double organicMl = volumeOf(Phase.ORGANIC) + other.volumeOf(Phase.ORGANIC);
    // rebuild our layers with everything combined (water at the bottom, ether on top)
    layers = buildLayers(aqueousMl, organicMl, aqueous, organic, solids);
    mixCount = 0;                     // something new went in, so it has to be mixed again

    // they poured everything in, so they're empty now (e.g. the pipette)
    other.layers = List.of();
    other.ph = 7;
  }

  // Helper Function
  // The same amounts, times a share (e.g. 0.5 for half the liquid).
  private Map<Substance, Double> scaled(Map<Substance, Double> amounts, double share) {
    Map<Substance, Double> result = new HashMap<>();
    amounts.forEach((substance, amount) -> result.put(substance, amount * share));
    return result;
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