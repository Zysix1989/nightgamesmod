package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.State;
import nightgames.match.Participant;

public class Craft extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 3199968029862277675L;

    public Craft() {
        super("Craft Potion");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().get(Attribute.Cunning) > 15 && !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Participant user) {
        user.getCharacter().state = State.crafting;
        return Movement.craft;
    }

    @Override
    public IMovement consider() {
        return Movement.craft;
    }

}
