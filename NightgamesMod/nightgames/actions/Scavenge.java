package nightgames.actions;

import nightgames.characters.State;
import nightgames.match.Participant;

public class Scavenge extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = -6692555226745083699L;

    public Scavenge() {
        super("Scavenge Items");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Participant user) {
        user.getCharacter().state = State.searching;
        return Movement.scavenge;
    }

}
