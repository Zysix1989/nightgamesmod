package nightgames.actions;

import nightgames.characters.Character;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.ftc.FTCEncounter;

import java.util.Optional;

public class PassAmbush extends Action {
    private static final long serialVersionUID = -1745311550506911281L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " slip into an alcove.";
        }
    }

    public static class State implements Participant.PState {
        @Override
        public nightgames.characters.State getEnum() {
            return nightgames.characters.State.inPass;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in an alcove in the pass.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
            assert encounter instanceof FTCEncounter;
            return Optional.of(() -> ((FTCEncounter) encounter).passAmbush(p, other));
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

    public PassAmbush() {
        super("Try Ambush");
    }

    @Override
    public boolean usable(Participant user) {
        return !(user.state instanceof State)
                && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You try to find a decent hiding place in the irregular rock faces lining the pass.");
        user.state = new State();
        return new Aftermath();
    }

}
