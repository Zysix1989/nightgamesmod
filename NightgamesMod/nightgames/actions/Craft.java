package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.State;
import nightgames.match.Participant;

public class Craft extends Action {
    private static final long serialVersionUID = 3199968029862277675L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {
            super(Movement.craft);
        }
    }

    public Craft() {
        super("Craft Potion");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().get(Attribute.Cunning) > 15 && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().state = State.crafting;
        return new Aftermath();
    }

}
