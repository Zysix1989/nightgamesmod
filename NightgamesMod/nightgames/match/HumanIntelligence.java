package nightgames.match;

import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.items.Item;
import nightgames.match.actions.Move;
import nightgames.modifier.action.DescribablePredicate;
import nightgames.trap.Trap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HumanIntelligence implements Intelligence {
    private Player character;

    public HumanIntelligence(Player character) {
        this.character = character;
    }

    @Override
    public void move(Collection<Action.Instance> possibleActions,
                     Consumer<Action.Instance> callback) {
        possibleActions.stream()
                .filter(act -> act.self instanceof Move)
                .map(act -> (Move.Instance) act)
                .filter(act -> act.maybeDetectOccupancy(character.get(Attribute.Perception)))
                .forEach(act -> {
                    character.message("You hear something in the <b>" + act.getDestination().name + "</b>.");
                    act.getDestination().setPinged(true);
                });
        presentMoveOptions(possibleActions.stream()
                        .filter(act ->
                                Global.getMatch().getCondition()
                                        .getActionFilterFor(character)
                                        .orElse(DescribablePredicate.True())
                                        .test(act))
                        .collect(Collectors.toSet()),
                callback);
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

    @Override
    public void showerScene(Participant target, Runnable ambushContinuation, Runnable stealContinuation, Runnable aphrodisiacContinuation, Runnable waitContinuation) {
        if (target.getLocation().name.equals("Showers")) {
            character.gui.message("You hear running water coming from the first floor showers. There shouldn't be any residents on this floor right now, so it's likely one "
                    + "of your opponents. You peek inside and sure enough, <b>" + target.getCharacter().subject()
                    + "</b> is taking a shower and looking quite vulnerable. Do you take advantage "
                    + "of her carelessness?");
        } else if (target.getLocation().name.equals("Pool")) {
            character.gui.message("You stumble upon <b>" + target.getCharacter().nameDirectObject()
                    + "</b> skinny dipping in the pool. She hasn't noticed you yet. It would be pretty easy to catch her off-guard.");
        }
        character.assessOpponent(target);
        character.gui.message("<br/>");

        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Surprise Her",
                character.encounterOption(() -> {
                    ambushContinuation.run();
                    Global.getMatch().resume();
                })));
        if (!target.getCharacter().mostlyNude()) {
            options.add(new CommandPanelOption("Steal Clothes",
                    character.encounterOption(() -> {
                        stealContinuation.run();
                        Global.getMatch().resume();
                    })));
        }
        if (character.has(Item.Aphrodisiac)) {
            options.add(new CommandPanelOption("Use Aphrodisiac",
                    character.encounterOption(() -> {
                        Global.getMatch().resume();
                    })));
        }
        options.add(new CommandPanelOption("Do Nothing",
                character.encounterOption(() -> {
                    waitContinuation.run();
                    Global.getMatch().resume();
                })));
        character.gui.presentOptions(options);
        Global.getMatch().pause();
    }


    @Override
    public void intrudeInCombat(Set<Encounter.IntrusionOption> intrusionOptions, List<Move.Instance> possibleMoves, Consumer<Action.Instance> actionCallback, Runnable neitherContinuation) {
        var listOptions = new ArrayList<>(intrusionOptions);
        assert listOptions.size() == 2: "No support for more than 2 combatants";
        character.gui.message("You find <b>" + listOptions.get(0).getTargetCharacter().getName() + "</b> and <b>" + listOptions.get(1).getTargetCharacter().getName()
                + "</b> fighting too intensely to notice your arrival. If you intervene now, it'll essentially decide the winner.");
        character.gui.message("Then again, you could just wait and see which one of them comes out on top. It'd be entertaining,"
                + " at the very least. Alternatively, you could just leave them to it.");

        ArrayList<CommandPanelOption> options = listOptions.stream()
                .map(option -> new CommandPanelOption("Help " + option.getTargetCharacter().getName(),
                        event -> {
                            character.gui.watchCombat(option.getRelevantCombat());
                            option.callback();
                        }))
                .collect(Collectors.toCollection(ArrayList::new));
        options.add(new CommandPanelOption("Watch them fight", event -> neitherContinuation.run()));
        options.addAll(possibleMoves.stream()
                .map(move -> new CommandPanelOption(move.self.name,
                        event -> {
                            actionCallback.accept(move);
                            Global.getMatch().resume();
                        }))
                .collect(Collectors.toSet()));
        Global.getMatch().pause();
        character.gui.presentOptions(options);
    }


    private void presentMoveOptions(Collection<Action.Instance> actionChoices,
                                    Consumer<Action.Instance> callback) {
        var optionChoices = actionChoices.stream()
                .map(action -> new CommandPanelOption(
                        action.toString(),
                        event -> {
                            callback.accept(action);
                            Global.getMatch().resume();
                        })).collect(Collectors.toList());
        assert !optionChoices.isEmpty();
        character.gui.presentOptions(optionChoices);
        Global.getMatch().pause();
    }
}
