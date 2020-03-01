package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.match.Participant;

public class Hide extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 9222848242102511020L;

    public Hide() {
        super("Hide");

    }

    @Override
    public boolean usable(Participant user) {
        return !(user.getCharacter().state == State.hidden) && !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Character user) {
        if (user.human()) {
            Global.gui().message("You find a decent hiding place and wait for unwary opponents.");
        }
        user.state = State.hidden;
        return Movement.hide;
    }

    @Override
    public IMovement consider() {
        return Movement.hide;
    }
}
