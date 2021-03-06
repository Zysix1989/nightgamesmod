package nightgames.match;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.actions.Move;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ArtificialIntelligence implements Intelligence {
    private NPC character;

    public ArtificialIntelligence(NPC character) {
        this.character = character;
    }

    @Override
    public void move(Collection<Action.Instance> possibleActions, Consumer<Action.Instance> callback) {
        Set<Area> radar = Set.of();
        if (!character.has(Trait.immobile)) {
            radar = possibleActions.stream()
                    .filter(act -> act instanceof Move.Instance)
                    .map(act -> (Move.Instance) act)
                    .filter(act -> act.maybeDetectOccupancy(character.get(Attribute.Perception)))
                    .map(Move.Instance::getDestination)
                    .collect(Collectors.toSet());
        }
        var chosenAction = character.ai.move(possibleActions, radar);
        callback.accept(chosenAction);
    }


    @Override
    public void promptTrap(Participant target, Runnable attackContinuation, Runnable waitContinuation) {
        if (character.ai.attack(target.getCharacter())) {
            attackContinuation.run();
        }
    }


    @Override
    public void faceOff(Participant opponent, Runnable fightContinuation, Runnable fleeContinuation, Runnable smokeContinuation) {
        if (character.ai.fightFlight(opponent.getCharacter())) {
            fightContinuation.run();
        } else if (character.has(Item.SmokeBomb)) {
            character.remove(Item.SmokeBomb);
            smokeContinuation.run();
        } else {
            fleeContinuation.run();
        }
    }

    @Override
    public void spy(Participant opponent, Runnable ambushContinuation, Runnable waitContinuation) {
        if (character.ai.attack(opponent.getCharacter())) {
            ambushContinuation.run();
        } else {
            waitContinuation.run();
        }
    }

    @Override
    public void showerScene(Participant target, Runnable ambushContinuation, Runnable stealContinuation, Runnable aphrodisiacContinuation, Runnable waitContinuation) {
        if (character.has(Item.Aphrodisiac)) {
            ambushContinuation.run();
        } else if (!target.getCharacter().mostlyNude() && Global.random(3) >= 2) {
            stealContinuation.run();
        } else {
            ambushContinuation.run();
        }
    }

    private static class IntrusionEvaluation {
        private Encounter.IntrusionOption option;
        private int score;

        IntrusionEvaluation(Encounter.IntrusionOption option, int score) {
            this.option = option;
            this.score = score;
        }
    }

    @Override
    public void intrudeInCombat(Set<Encounter.IntrusionOption> intrusionOptions, List<Move.Instance> possibleMoves, Consumer<Action.Instance> actionCallback, Runnable neitherContinuation) {
        var bestTarget = intrusionOptions.stream()
                .map(option -> new IntrusionEvaluation(option,
                        Global.random(20) +
                                character.getAffection(option.getTargetCharacter()) +
                                (option.getTargetCharacter().has(Trait.sympathetic) ? 10 : 0)))
                .reduce(new IntrusionEvaluation(null, Integer.MIN_VALUE),
                        (best, current) -> best.score >= current.score ? best : current);
        bestTarget.option.callback();
    }

    @Override
    public nightgames.combat.ArtificialIntelligence makeCombatIntelligence() {
        return new nightgames.combat.ArtificialIntelligence(character);
    }

}
