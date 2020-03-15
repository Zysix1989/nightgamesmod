package nightgames.match.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Scavenge extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " begin scrounging through some boxes in the corner.";
        }
    }

    public final class Instance extends Action.Instance {
        public final Action self;

        private Instance(Action self, Participant user) {
            super(user);
            this.self = self;
        }

        @Override
        public Action.Aftermath execute() {
            return executeOuter(user);
        }
    }

    public static class State implements Participant.State {

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
            Character character = p.getCharacter();
            foundItems.forEach(character::gain);
            if (foundItems.isEmpty()) {
                character.message("You don't find anything useful.");
            }
            p.state = new Ready();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            return Optional.of(() -> encounter.spy(other, p));
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("spot check for %s should have already been replaced",
                    p.getCharacter().getTrueName()));
        }

    }

    public Scavenge() {
        super("Scavenge Items");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        user.state = new State();
        return new Aftermath();
    }

}
