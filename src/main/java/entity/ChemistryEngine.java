package entity;

import java.util.List;

/**
 * Handles chemistry reactions. When an allowed combination is done, this class will perform:
 * adding it and applying reaction rules.
 */
public class ChemistryEngine {
  private final List<ReactionRule> rules;

  public ChemistryEngine(List<ReactionRule> rules) {
    this.rules = rules;
  }

  /**
   * Adds a reagent to the mixture and applies applicable reaction rules.
   *
   * @param mixture the mixture to which the reagent is added
   * @param substance the reagent being added
   * @param amount mL for water or ether, mmol for anything else
   */
  public void addReagent(Mixture mixture, Substance substance, double amount) {
    mixture.add(substance, amount);
    for (ReactionRule rule : rules) {
      if (rule.appliesTo(mixture, substance)) {
        rule.apply(mixture, substance, amount);
      }
    }
  }
}