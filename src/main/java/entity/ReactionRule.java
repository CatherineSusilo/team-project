package entity;

/**
 * Defines a reaction rule (what happens to a mixture when a certain substance is added to it.)
 */
public interface ReactionRule {

  /**
   * Whether this rule does anything when the substance is added to the mixture.
   *
   * @param mixture what is in the container
   * @param added what was just added
   * @return true if this rule should run
   */
  boolean appliesTo(Mixture mixture, Substance added);

  /**
   * Changes the mixture because the substance was added.
   *
   * @param mixture what is in the container
   * @param added what was just added
   * @param amount how much was added
   */
  void apply(Mixture mixture, Substance added, double amount);
}