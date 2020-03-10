package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.match.ftc.FTCEncounter;

import java.util.Optional;

public class TreeAmbush extends Action {
    private static final long serialVersionUID = -8503564080765172483L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " climb up a tree.";
        }
    }

    public static class State implements Participant.PState {

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in a tree, waiting to drop down on an unwitting foe.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
            assert encounter instanceof FTCEncounter;
            return Optional.of(() -> ((FTCEncounter) encounter).treeAmbush(p, other));
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

    public TreeAmbush() {
        super("Climb a Tree");
    }

    @Override
    public boolean usable(Participant user) {
        return (user.getCharacter().get(Attribute.Power) >= 20 || user.getCharacter().get(Attribute.Animism) >= 10)
                        && !(user.state instanceof State)
                        && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().get(Attribute.Animism) >= 10) {
            user.getCharacter().message("Following your instincts, you clamber up a tree" + " to await an unwitting passerby.");
        } else {
            user.getCharacter().message("You climb up a tree that has a branch hanging over"
                    + " the trail. It's hidden in the leaves, so you should be"
                    + " able to surprise someone passing underneath.");
        }
        user.state = new State();
        return new Aftermath();
    }

}
