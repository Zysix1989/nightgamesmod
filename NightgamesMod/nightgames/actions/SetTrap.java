package nightgames.actions;

import nightgames.global.Global;
import nightgames.match.Participant;
import nightgames.trap.Trap;

public class SetTrap extends Action {
    /**
     * 
     */
    private static final long serialVersionUID = 9194305067966782124L;
    private Trap trap;

    public SetTrap(Trap trap) {
        super("Set(" + trap.toString() + ")");
        this.trap = trap;
    }

    @Override
    public boolean usable(Participant user) {
        return trap.recipe(user) && !user.getLocation().open()
                && trap.requirements(user)
                && !user.getCharacter().bound()
                && user.getLocation().getTrap().isEmpty();
    }

    @Override
    public IMovement execute(Participant user) {
        var result = trap.instantiate(user);
        user.getLocation().setTrap(result.instance);
        if (user.getCharacter().human()) {
            Global.gui().message(result.message);
        }
        return Movement.trap;
    }

}
