package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.status.Oiled;

public class UseLubricant extends Action {

    public UseLubricant() {
        super("Oil up");
    }

    @Override
    public boolean usable(Character user) {
        return !user.bound();
    }

    @Override
    public IMovement execute(Character user) {
        if (user.human()) {
            Global.gui().message(
                "You cover yourself in slick oil. It's a weird feeling, but it should make it easier to escape from a hold.");
        }
        user.addNonCombat(new Oiled(user));
        user.consume(Item.Lubricant, 1);
        return Movement.oil;
    }

    @Override
    public IMovement consider() {
        return Movement.oil;
    }
}
