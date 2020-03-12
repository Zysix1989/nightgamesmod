package nightgames.match;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.items.Item;
import nightgames.trap.Trap;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class ArtificialIntelligence implements Intelligence {
    private NPC character;

    public ArtificialIntelligence(NPC character) {
        this.character = character;
    }

    @Override
    public void move(Collection<Action> possibleActions, Consumer<Action> callback) {
        HashSet<Area> radar = new HashSet<>();
        if (!character.has(Trait.immobile)) {
            radar.addAll(character.location.get().noisyNeighbors(character.get(Attribute.Perception)));
        }
        var chosenAction = character.ai.move(possibleActions, radar);
        callback.accept(chosenAction);
    }


    @Override
    public void promptTrap(Participant target, Trap.Instance trap, Runnable attackContinuation, Runnable waitContinuation) {
        if (character.ai.attack(target.getCharacter())) {
            attackContinuation.run();
        } else {
            character.location.get().endEncounter();
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

}
