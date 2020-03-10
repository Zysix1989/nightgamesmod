package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;

import java.io.Serializable;
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

    public static class Ready implements Participant.PState {
        @Override
        public State getEnum() {
            return State.ready;
        }

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
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
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

    public abstract Aftermath execute(Participant user);

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Action other = (Action) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
