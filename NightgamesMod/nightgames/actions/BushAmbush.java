package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.match.Participant;

public class BushAmbush extends Action {
    private static final long serialVersionUID = 2384434976695344978L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {

        }

        @Override
        public String describe(Character c) {
            return " dive into some bushes.";
        }
    }

    public BushAmbush() {
        super("Hide in Bushes");
    }

    @Override
    public boolean usable(Participant user) {
        return (user.getCharacter().get(Attribute.Cunning) >= 20 || user.getCharacter().get(Attribute.Animism) >= 10)
                && user.state.getEnum() != State.inBushes
                && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().get(Attribute.Animism) >= 10) {
            user.getCharacter().message("You crouch down in some dense bushes, ready" + " to pounce on passing prey.");
        } else {
            user.getCharacter().message("You spot some particularly dense bushes, and figure"
                    + " they'll make for a decent hiding place. You lie down in them,"
                    + " and wait for someone to walk past.");
        }
        user.state = new Participant.InBushesState();
        return new Aftermath();
    }

}
