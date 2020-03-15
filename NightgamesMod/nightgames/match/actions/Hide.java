package nightgames.match.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;

import java.util.Optional;

public class Hide extends Action {
    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() { }

        @Override
        public String describe(Character c) {
            return " disappear into a hiding place.";
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
            return self.executeOuter(user);
        }
    }

    public static class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You have found a hiding spot and are waiting for someone to pounce upon.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            return (p.getCharacter().get(Attribute.Cunning) * 2 / 3) + 20;
        }
    }

    public Hide() {
        super("Hide");
    }

    @Override
    public boolean usable(Participant user) {
        return !(user.state instanceof State) && !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        user.getCharacter().message("You find a decent hiding place and wait for unwary opponents.");
        user.state = new State();
        return new Aftermath();
    }

}
