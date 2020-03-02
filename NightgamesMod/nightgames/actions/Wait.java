package nightgames.actions;

import nightgames.match.Participant;

public class Wait extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = -644996487174479671L;

    public Wait() {
        super("Wait");
    }

    @Override
    public boolean usable(Participant user) {
        return true;
    }

    @Override
    public IMovement execute(Participant user) {
        return Movement.wait;
    }

    @Override
    public IMovement consider() {
        return Movement.wait;
    }

}
