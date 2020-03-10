package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.match.Participant;

public class Hide extends Action {
    private static final long serialVersionUID = 9222848242102511020L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() { }

        @Override
        public String describe(Character c) {
            return " disappear into a hiding place.";
        }
    }

    public Hide() {
        super("Hide");
    }

    @Override
    public boolean usable(Participant user) {
        return !(user.state == State.hidden) && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You find a decent hiding place and wait for unwary opponents.");
        user.state = State.hidden;
        return new Aftermath();
    }

}
