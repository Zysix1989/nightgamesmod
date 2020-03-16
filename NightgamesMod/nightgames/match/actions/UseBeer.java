package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Buzzed;

public class UseBeer extends Action {

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            user.getCharacter().message("You pop open a beer and chug it down, feeling buzzed and a bit slugish.");
            user.getCharacter().addNonCombat(new Status(new Buzzed(user.getCharacter())));
            user.getCharacter().consume(Item.Beer, 1);
            messageOthersInLocation(user.getCharacter().getGrammar().subject().defaultNoun() +
                    " opening a beer and downing the whole thing.");
        }
    }

    public UseBeer() {
        super( "Beer");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
