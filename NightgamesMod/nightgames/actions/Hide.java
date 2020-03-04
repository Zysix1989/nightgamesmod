package nightgames.actions;

import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.match.Participant;

public class Hide extends Action {
    private static final long serialVersionUID = 9222848242102511020L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {
            super(Movement.hide);
        }
    }

    public Hide() {
        super("Hide");
    }

    @Override
    public boolean usable(Participant user) {
        return !(user.getCharacter().state == State.hidden) && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().human()) {
            Global.gui().message("You find a decent hiding place and wait for unwary opponents.");
        }
        user.getCharacter().state = State.hidden;
        return new Aftermath();
    }

}
