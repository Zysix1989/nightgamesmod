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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
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
            p.character.search(p.searchItems());
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
            p.character.craft(p.craftItems());
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
            p.resupply();
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
    // Participants this participant has defeated recently.  They are not valid targets until they resupply.
    private CopyOnWriteArrayList<Character> mercy = new CopyOnWriteArrayList<>();

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
        this.mercy = new CopyOnWriteArrayList<>(p.mercy);
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

    public void defeated(Participant p) {
        assert !mercy.contains(p.getCharacter());
        mercy.addIfAbsent(p.getCharacter());
        incrementScore(1);
    }

    public Participant copy() {
        return new Participant(this);
    }

    void allowTarget(Participant p) {
        mercy.remove(p.getCharacter());
    }

    public void place(Area loc) {
        character.location.set(loc);
        loc.place(this);
        if (loc.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public boolean canStartCombat(Participant p2) {
        return !mercy.contains(p2.getCharacter()) && state.getEnum() != State.resupplying;
    }

    public interface ActionCallback {
        Action.Aftermath execute(Action a);
    }


    private Collection<Item> craftItems() {
        int roll = Global.random(15);
        if (character.check(Attribute.Cunning, 25)) {
            if (roll == 9) {
                return List.of(Item.Aphrodisiac, Item.DisSol);
            } else if (roll >= 5) {
                return List.of(Item.Aphrodisiac);
            } else {
                return List.of(Item.Lubricant, Item.Sedative);
            }
        } else if (character.check(Attribute.Cunning, 20)) {
            if (roll == 9) {
                return List.of(Item.Aphrodisiac);
            } else if (roll >= 7) {
                return List.of(Item.DisSol);
            } else if (roll >= 5) {
                return List.of(Item.Lubricant);
            } else if (roll >= 3) {
                return List.of(Item.Sedative);
            } else {
                return List.of(Item.EnergyDrink);
            }
        } else if (character.check(Attribute.Cunning, 15)) {
            if (roll == 9) {
                return List.of(Item.Aphrodisiac);
            } else if (roll >= 8) {
                return List.of(Item.DisSol);
            } else if (roll >= 7) {
                return List.of(Item.Lubricant);
            } else if (roll >= 6) {
                return List.of(Item.EnergyDrink);
            }
        } else {
            if (roll >= 7) {
                return List.of(Item.Lubricant);
            } else if (roll >= 5) {
                return List.of(Item.Sedative);
            }
        }
        return List.of();
    }

    private Collection<Item> searchItems() {
        int roll = Global.random(10);
        switch (roll) {
            case 9:
                return List.of(Item.Tripwire, Item.Tripwire);
            case 8:
                return List.of(Item.ZipTie, Item.ZipTie, Item.ZipTie);
            case 7:
                return List.of(Item.Phone);
            case 6:
                return List.of(Item.Rope);
            case 5:
                return List.of(Item.Spring);
            default:
                return List.of();
        }
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

    public void resupply() {
        mercy.clear();
        character.change();
        state = new ReadyState();
        character.getWillpower().renew();
        if (character.location().getOccupants().size() > 1) {
            if (character.location().id() == AreaIdentity.dorm) {
                if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                    travel(Global.getMatch().gps("Quad").orElseThrow(),
                            "You hear your opponents searching around the "
                                    + "dorm, so once you finish changing, you hop out the window and "
                                    + "head to the quad.");
                } else {
                    travel(Global.getMatch().gps("Laundry").orElseThrow(),
                            "You hear your opponents searching around "
                                    + "the dorm, so once you finish changing, you quietly move "
                                    + "downstairs to the laundry room.");
                }
            }
            if (character.location().id() == AreaIdentity.union) {
                if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                    travel(Global.getMatch().gps("Quad").orElseThrow(),
                            "You don't want to be ambushed leaving the "
                                    + "student union, so once you finish changing, you hop out the "
                                    + "window and head to the quad.");
                } else {
                    travel(Global.getMatch().gps("Pool").orElseThrow(),
                            "You don't want to be ambushed leaving "
                                    + "the student union, so once you finish changing, you sneak out "
                                    + "the back door and head to the pool.");
                }
            }
        }
    }

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

    public void invalidateTarget(Participant victor) {
        mercy.addIfAbsent(victor.getCharacter());
    }

    void finishMatch() {
        for (var victor : mercy) {
            victor.bounty( 1, victor);
        }
        character.finishMatch();
        mercy.clear();
    }

}
