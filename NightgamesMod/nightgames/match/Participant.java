package nightgames.match;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.areas.Area;
import nightgames.areas.AreaIdentity;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.status.Stsflag;

import java.util.*;
import java.util.stream.Collectors;

public class Participant {
    public interface PState {
        State getEnum();
        boolean allowsNormalActions();
        void move(Participant p);
        boolean isDetectable();
    }

    public static class ReadyState implements PState {
        @Override
        public State getEnum() {
            return State.ready;
        }

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {}

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class ShowerState implements PState {
        @Override
        public State getEnum() {
            return State.shower;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.bathe(getEnum());
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class CombatState implements PState {
        @Override
        public State getEnum() {
            return State.combat;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.location.get().fight.battle();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class SearchingState implements PState {
        @Override
        public State getEnum() {
            return State.searching;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            Collection<Item> foundItems;
            int roll = Global.random(10);
            switch (roll) {
                case 9:
                    foundItems = List.of(Item.Tripwire, Item.Tripwire);
                    break;
                case 8:
                    foundItems = List.of(Item.ZipTie, Item.ZipTie, Item.ZipTie);
                    break;
                case 7:
                    foundItems = List.of(Item.Phone);
                    break;
                case 6:
                    foundItems = List.of(Item.Rope);
                    break;
                case 5:
                    foundItems = List.of(Item.Spring);
                    break;
                default:
                    foundItems = List.of();
                    break;
            }
            p.character.search(foundItems);
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class CraftingState implements PState {
        @Override
        public State getEnum() {
            return State.crafting;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            Collection<Item> craftedItems;
            int roll = Global.random(15);
            if (p.character.check(Attribute.Cunning, 25)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac, Item.DisSol);
                } else if (roll >= 5) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else {
                    craftedItems = List.of(Item.Lubricant, Item.Sedative);
                }
            } else if (p.character.check(Attribute.Cunning, 20)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else if (roll >= 7) {
                    craftedItems = List.of(Item.DisSol);
                } else if (roll >= 5) {
                    craftedItems = List.of(Item.Lubricant);
                } else if (roll >= 3) {
                    craftedItems = List.of(Item.Sedative);
                } else {
                    craftedItems = List.of(Item.EnergyDrink);
                }
            } else if (p.character.check(Attribute.Cunning, 15)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else if (roll >= 8) {
                    craftedItems = List.of(Item.DisSol);
                } else if (roll >= 7) {
                    craftedItems = List.of(Item.Lubricant);
                } else if (roll >= 6) {
                    craftedItems = List.of(Item.EnergyDrink);
                } else {
                    craftedItems = List.of();
                }
            } else if (roll >= 7) {
                craftedItems = List.of(Item.Lubricant);
            } else if (roll >= 5) {
                craftedItems = List.of(Item.Sedative);
            } else {
                craftedItems = List.of();
            }
            p.character.craft(craftedItems);
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class HiddenState implements PState {
        @Override
        public State getEnum() {
            return State.hidden;
        }

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.character.message("You have found a hiding spot and are waiting for someone to pounce upon.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }
    }

    public static class ResupplyingState implements PState {
        @Override
        public State getEnum() {
            return State.resupplying;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.invalidAttackers.clear();
            p.character.change();
            p.state = new ReadyState();
            p.character.getWillpower().renew();
            if (p.character.location().getOccupants().size() > 1) {
                if (p.character.location().id() == AreaIdentity.dorm) {
                    if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                        p.travel(Global.getMatch().gps("Quad").orElseThrow(),
                                "You hear your opponents searching around the "
                                        + "dorm, so once you finish changing, you hop out the window and "
                                        + "head to the quad.");
                    } else {
                        p.travel(Global.getMatch().gps("Laundry").orElseThrow(),
                                "You hear your opponents searching around "
                                        + "the dorm, so once you finish changing, you quietly move "
                                        + "downstairs to the laundry room.");
                    }
                }
                if (p.character.location().id() == AreaIdentity.union) {
                    if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                        p.travel(Global.getMatch().gps("Quad").orElseThrow(),
                                "You don't want to be ambushed leaving the "
                                        + "student union, so once you finish changing, you hop out the "
                                        + "window and head to the quad.");
                    } else {
                        p.travel(Global.getMatch().gps("Pool").orElseThrow(),
                                "You don't want to be ambushed leaving "
                                        + "the student union, so once you finish changing, you sneak out "
                                        + "the back door and head to the pool.");
                    }
                }
            }
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class LostClothesState implements PState {
        @Override
        public State getEnum() {
            return State.lostclothes;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.bathe(getEnum());
            p.character.message("Your clothes aren't where you left them. Someone must have come by and taken them.");
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class WebbedState implements PState {
        @Override
        public State getEnum() {
            return State.webbed;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.message("You eventually manage to get an arm free, which you then use to extract yourself from the trap.");
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class MasturbatingState implements PState {
        @Override
        public State getEnum() {
            return State.masturbating;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.masturbate();
            p.state = new ReadyState();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }
    }

    public static class InTreeState implements PState {
        @Override
        public State getEnum() {
            return State.inTree;
        }

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.character.message("You are hiding in a tree, waiting to drop down on an unwitting foe.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }
    }

    public static class InBushesState implements PState {
        @Override
        public State getEnum() {
            return State.inBushes;
        }

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.character.message("You are hiding in dense bushes, waiting for someone to pass by.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }
    }

    public static class InPassState implements PState {
        @Override
        public State getEnum() {
            return State.inPass;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.character.message("You are hiding in an alcove in the pass.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }
    }



    protected Character character;
    private int score = 0;
    private int roundsToWait = 0;
    public PState state = new ReadyState();
    private Set<Participant> invalidAttackers = new HashSet<>();

    public Participant(Character c) {
        this.character = c;
    }

    Participant(Participant p) {
        try {
            this.character = this.getCharacter().clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.score = p.score;
        this.roundsToWait = p.roundsToWait;
        this.state = p.state;
        this.invalidAttackers = new HashSet<>(p.invalidAttackers);
    }

    public Character getCharacter() {
        return character;
    }

    int getScore() {
        return score;
    }

    public void incrementScore(int i) {
        score += i;
        getLocation()
                .getOccupants()
                .forEach(p -> p.getCharacter().message(Match.scoreString(getCharacter(), i)));
    }

    public Participant copy() {
        return new Participant(this);
    }

    public void place(Area loc) {
        character.location.set(loc);
        loc.place(this);
        if (loc.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public boolean canStartCombat(Participant p2) {
        return !p2.invalidAttackers.contains(this) && state.getEnum() != State.resupplying;
    }

    public interface ActionCallback {
        Action.Aftermath execute(Action a);
    }

    public void move() {
        character.displayStateMessage(character.location.get().getTrap(this));

        var possibleActions = new ArrayList<Action>();
        possibleActions.addAll(character.location.get().possibleActions(this));
        possibleActions.addAll(character.getItemActions());
        possibleActions.addAll(Global.getMatch().getAvailableActions());
        possibleActions.removeIf(a -> !a.usable(this));
        if (state.getEnum() == State.combat) {
            state.move(this);
        } else if (roundsToWait > 0) {
            roundsToWait--;
            return;
        } else if (this.character.is(Stsflag.enthralled)) {
            character.handleEnthrall(act -> act.execute(this));
            return;
        } else {
            state.move(this);
        }
        if (state.allowsNormalActions()) {
            if (character.location.get().encounter(this)) {
                character.move(possibleActions, act -> act.execute(this));
            }
        }
    }

    public void flee() {
        var options = character.location.get().possibleActions(this);
        var destinations = options.stream()
                .filter(action -> action instanceof Move)
                .map(action -> (Move) action)
                .map(Move::getDestination)
                .collect(Collectors.toList());
        var destination = destinations.get(Global.random(destinations.size()));
        travel(destination, "You dash away and escape into the <b>" + destination.name + ".</b>");
    }

    public void waitRounds(int i) {
        roundsToWait += i;
    }

    public void endOfMatchRound() {
        character.getTraits().forEach(trait -> {
            if (trait.status != null) {
                nightgames.status.Status newStatus = trait.status.instance(character, null);
                if (!character.has(newStatus)) {
                    character.addNonCombat(new nightgames.match.Status(newStatus));
                }
            }
        });
        character.endOfMatchRound();
    }

    public Area getLocation() { return character.location(); }

    public void travel(Area dest) {
        state = new ReadyState();
        character.location.get().exit(this.character);
        character.location.set(dest);
        dest.enter(this.character);
        if (dest.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public void travel(Area dest, String message) {
        travel(dest);
        character.notifyTravel(dest, message);
    }

    public void timePasses() {}

    public void interveneInCombat(Set<DefaultEncounter.IntrusionOption> intrusionOptions, Runnable noneContinuation) {
        character.intrudeInCombat(intrusionOptions,
                character.location.get().possibleActions(this).stream()
                        .filter(act -> act instanceof Move)
                        .map(act -> (Move) act)
                        .collect(Collectors.toList()), act -> act.execute(this), noneContinuation
        );
    }

    public void invalidateAttacker(Participant victor) {
        invalidAttackers.add(victor);
    }

    void finishMatch() {
        character.finishMatch();
        invalidAttackers.clear();
    }

    public int pointsForVictory(Participant loser) {
        return loser.pointsGivenToVictor();
    }

    protected int pointsGivenToVictor() {
        return 1;
    }
}
