package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.match.Participant;

public class PassAmbush extends Action {
    private static final long serialVersionUID = -1745311550506911281L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " slip into an alcove.";
        }
    }

    public PassAmbush() {
        super("Try Ambush");
    }

    @Override
    public boolean usable(Participant user) {
        return user.state.getEnum() != State.inPass
                && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You try to find a decent hiding place in the irregular rock faces lining the pass.");
        user.state = new Participant.InPassState();
        return new Aftermath();
    }

}
