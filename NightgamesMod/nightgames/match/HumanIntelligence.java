package nightgames.match;

import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.trap.Trap;

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

    @Override
    public void promptTrap(Participant target, Trap.Instance trap, Runnable attackContinuation, Runnable waitContinuation) {
        character.message("Do you want to take the opportunity to ambush <b>" + target.getCharacter().getName() + "</b>?");
        character.assessOpponent(target);
        character.message("<br/>");

        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Attack " + target.getCharacter().getName(),
                character.encounterOption(() -> {
                    attackContinuation.run();
                    Global.getMatch().resume();
                })));
        options.add(new CommandPanelOption("Wait",
                character.encounterOption(() -> {
                    waitContinuation.run();
                    Global.getMatch().resume();
                })));
        character.gui.presentOptions(options);
        Global.getMatch().pause();
    }


    @Override
    public void faceOff(Participant opponent, Runnable fightContinuation, Runnable fleeContinuation, Runnable smokeContinuation) {
        character.gui.message("You run into <b>" + opponent.getCharacter().nameDirectObject()
                + "</b> and you both hesitate for a moment, deciding whether to attack or retreat.");
        character.presentFightFlightChoice(opponent, character.encounterOption(() -> {
            fightContinuation.run();
            Global.getMatch().resume();
        }), character.encounterOption(() -> {
            fleeContinuation.run();
            Global.getMatch().resume();
        }));
        Global.getMatch().pause();
    }

    @Override
    public void spy(Participant opponent, Runnable ambushContinuation, Runnable waitContinuation) {
        character.gui.message("You spot <b>" + opponent.getCharacter().nameDirectObject()
                + "</b> but she hasn't seen you yet. You could probably catch her off guard, or you could remain hidden and hope she doesn't notice you.");
        character.assessOpponent(opponent);
        character.gui.message("<br/>");
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Ambush",
                character.encounterOption(() -> {
                    ambushContinuation.run();
                    Global.getMatch().resume();
                })));
        options.add(new CommandPanelOption("Wait",
                character.encounterOption(() -> {
                    waitContinuation.run();
                    Global.getMatch().resume();
                })));
        character.gui.presentOptions(options);
        Global.getMatch().pause();
    }
}
