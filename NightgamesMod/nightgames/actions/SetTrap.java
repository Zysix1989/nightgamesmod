package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
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
    public boolean usable(Character user) {
        return trap.recipe(user) && !user.location().open() && trap.requirements(user)
                        && user.location().env.size() < 5 && !user.bound() 
                        && !user.location().isTrapped();
    }

    @Override
    public IMovement execute(Character user) {
        var result = trap.instantiate(user);
        user.location().place(result.instance);
        if (user.human()) {
            Global.gui().message(result.message);
        }
        return Movement.trap;
    }

    @Override
    public IMovement consider() {
        return Movement.trap;
    }

}
