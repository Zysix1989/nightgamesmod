package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.status.Buzzed;

public class UseBeer extends Action {

    public UseBeer() {
        super("Use " + Item.Beer.getName());
        name = "Beer";
    }

    @Override
    public boolean usable(Character user) {
        return user.has(Item.Beer) && !user.bound();
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
