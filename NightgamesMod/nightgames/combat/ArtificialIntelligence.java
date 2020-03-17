package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.global.Global;
import nightgames.skills.Nothing;
import nightgames.skills.Skill;
import nightgames.skills.strategy.CombatStrategy;
import nightgames.skills.strategy.DefaultStrategy;

import java.util.*;

public class ArtificialIntelligence implements Intelligence {
    private final NPC character;

    public ArtificialIntelligence(NPC character) {
        this.character = character;
    }

    public boolean act(Combat c, Character target) {
        CombatantData combatantData = c.getCombatantData(character);

        // if there's no strategy, try getting a new one.
        if (!combatantData.hasStrategy()) {
            combatantData.setStrategy(c, character, pickStrategy(c));
        }
        CombatStrategy strategy = combatantData.getStrategy().get();

        // if the strategy is out of moves, try getting a new one.
        Collection<Skill> possibleSkills = strategy.nextSkills(c, character);
        if (possibleSkills.isEmpty()) {
            strategy = combatantData.setStrategy(c, character, pickStrategy(c));
            possibleSkills = strategy.nextSkills(c, character);
        }

        // if there are still no moves, just use all available skills for this turn and try again next turn.
        if (possibleSkills.isEmpty()) {
            possibleSkills = character.getSkills();
        }
        HashSet<Skill> available = new HashSet<>();
        for (Skill act : possibleSkills) {
            if (Skill.isUsable(c, act) && character.cooldownAvailable(act)) {
                available.add(act);
            }
        }
        Skill.filterAllowedSkills(c, available, character, target);
        if (available.size() == 0) {
            available.add(new Nothing(character));
        }
        c.act(character, character.ai.act(available, c));
        return false;
    }

    /**
     * We choose a random strategy from union of the defaults and this NPC's
     * personal strategies. The weights given to each strategy are dynamic,
     * calculated from the state of the given Combat.
     */
    private CombatStrategy pickStrategy(Combat c) {
        if (Global.random(100) < 60 ) {
            // most of the time don't bother using a strategy.
            return new DefaultStrategy();
        }
        Map<Double, CombatStrategy> stratsWithCumulativeWeights = new HashMap<>();
        DefaultStrategy defaultStrat = new DefaultStrategy();
        double lastWeight = defaultStrat.weight(c, character);
        stratsWithCumulativeWeights.put(lastWeight, defaultStrat);
        List<CombatStrategy> allStrategies = new ArrayList<>(CombatStrategy.availableStrategies);
        allStrategies.addAll(character.personalStrategies);
        for (CombatStrategy strat: allStrategies) {
            if (strat.weight(c, character) < .01 || strat.nextSkills(c, character).isEmpty()) {
                continue;
            }
            lastWeight += strat.weight(c, character);
            stratsWithCumulativeWeights.put(lastWeight, strat);
        }
        double random = Global.randomdouble() * lastWeight;
        for (Map.Entry<Double, CombatStrategy> entry: stratsWithCumulativeWeights.entrySet()) {
            if (random < entry.getKey()) {
                return entry.getValue();
            }
        }
        // we should have picked something, but w/e just return the default if we need to
        return defaultStrat;
    }
}
