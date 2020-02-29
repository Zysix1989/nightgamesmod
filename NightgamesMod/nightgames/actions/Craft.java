package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;

public class Craft extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 3199968029862277675L;

    public Craft() {
        super("Craft Potion");
    }

    @Override
    public boolean usable(Character user) {
        return user.get(Attribute.Cunning) > 15 && !user.bound();
    }

    @Override
    public IMovement execute(Character user) {
        user.state = State.crafting;
        return Movement.craft;
    }

    @Override
    public IMovement consider() {
        return Movement.craft;
    }

}
