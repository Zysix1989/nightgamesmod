package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.status.Buzzed;

public class UseBeer extends Action {

    public UseBeer() {
        super( "Beer");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Character user) {
        if (user.human()) {
            Global.gui().message("You pop open a beer and chug it down, feeling buzzed and a bit slugish.");
        }
        user.addNonCombat(new Buzzed(user));
        user.consume(Item.Beer, 1);
        return Movement.beer;
    }

    @Override
    public IMovement consider() {
        return Movement.beer;
    }

}
