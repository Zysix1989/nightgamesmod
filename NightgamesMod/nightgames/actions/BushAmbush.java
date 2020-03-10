package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.ftc.FTCEncounter;

import java.util.Optional;

public class BushAmbush extends Action {
    private static final long serialVersionUID = 2384434976695344978L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {

        }

        @Override
        public String describe(Character c) {
            return " dive into some bushes.";
        }
    }

    public static class State implements Participant.PState {
        @Override
        public nightgames.characters.State getEnum() {
            return nightgames.characters.State.inBushes;
        }

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in dense bushes, waiting for someone to pass by.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
            assert encounter instanceof FTCEncounter;
            return Optional.of(() -> ((FTCEncounter) encounter).bushAmbush(p, other));
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
    public BushAmbush() {
        super("Hide in Bushes");
    }

    @Override
    public boolean usable(Participant user) {
        return (user.getCharacter().get(Attribute.Cunning) >= 20 || user.getCharacter().get(Attribute.Animism) >= 10)
                && !(user.state instanceof State)
                && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().get(Attribute.Animism) >= 10) {
            user.getCharacter().message("You crouch down in some dense bushes, ready" + " to pounce on passing prey.");
        } else {
            user.getCharacter().message("You spot some particularly dense bushes, and figure"
                    + " they'll make for a decent hiding place. You lie down in them,"
                    + " and wait for someone to walk past.");
        }
        user.state = new State();
        return new Aftermath();
    }

}
