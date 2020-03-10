package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.match.Participant;

public class Scavenge extends Action {
    private static final long serialVersionUID = -6692555226745083699L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " begin scrounging through some boxes in the corner.";
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
    public Action.Aftermath execute(Participant user) {
        user.state = new Participant.SearchingState();
        return new Aftermath();
    }

}
