package nightgames.match;

import nightgames.characters.Character;

import java.util.Optional;

public abstract class Action {
    protected final String name;

    protected Action(String name) {
        assert name != null;
        this.name = name;
    }

    public abstract boolean usable(Participant user);

    public static abstract class Aftermath {

        protected Aftermath() {}

        public abstract String describe(Character c);
    }

    public abstract static class Instance {
        public final Action self;
        protected final Participant user;

        protected Instance(Action self, Participant user) {
            this.self = self;
            this.user = user;
        }

        public Aftermath execute() {
            return self.execute(user);
        }
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

    public abstract Instance newInstance(Participant user);

    public abstract Aftermath execute(Participant user);

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Action && name.equals(((Action) obj).name);
    }

}
