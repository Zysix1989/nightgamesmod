package nightgames.actions;

import nightgames.characters.Character;
import nightgames.match.Encounter;
import nightgames.match.Participant;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public abstract class Action implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4981682001213276175L;
    protected String name;

    public Action(String name) {
        this.name = name;
    }

    public abstract boolean usable(Participant user);

    public static abstract class Aftermath {

        protected Aftermath() {}

        public abstract String describe(Character c);
    }

    public static class Ready implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
        }

        @Override
        public boolean isDetectable() {
            return true;
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
            return 0;
        }
    }

    public static abstract class Busy implements Participant.State {
        private int roundsToWait;

        protected Busy(int roundsToWait) {
            this.roundsToWait = roundsToWait;
        }

        @Override
        public boolean allowsNormalActions() {
            return roundsToWait <= 0;
        }

        @Override
        public final void move(Participant p) {
            if (roundsToWait-- <= 0) {
                moveAfterDelay(p);
            }
        }

        protected abstract void moveAfterDelay(Participant p);
    }

    public abstract Aftermath execute(Participant user);

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        } else {
            return name.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Action && (Objects.equals(name, ((Action) obj).name));
    }

}
