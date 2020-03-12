package nightgames.match;

import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class HumanIntelligence implements Intelligence {
    private Player character;

    public HumanIntelligence(Player character) {
        this.character = character;
    }

    @Override
    public void move(Collection<Action> possibleActions,
                     Consumer<Action> callback) {
        var actionChoices = new ArrayList<Action>();
        character.location.get().noisyNeighbors(character.get(Attribute.Perception)).forEach(room -> {
            character.message("You hear something in the <b>" + room.name + "</b>.");
            room.setPinged(true);
        });
        for (Action act : possibleActions) {
            if (Global.getMatch().getCondition().allowAction(act, character, Global.getMatch())) {
                actionChoices.add(act);
            }
        }
        character.presentMoveOptions(actionChoices, callback);
    }
}
