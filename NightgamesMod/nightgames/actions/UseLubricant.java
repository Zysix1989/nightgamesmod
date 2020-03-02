package nightgames.actions;

import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.status.Oiled;

public class UseLubricant extends Action {

    public UseLubricant() {
        super("Oil up");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Participant user) {
        if (user.getCharacter().human()) {
            Global.gui().message(
                "You cover yourself in slick oil. It's a weird feeling, but it should make it easier to escape from a hold.");
        }
        user.getCharacter().addNonCombat(new Oiled(user.getCharacter()));
        user.getCharacter().consume(Item.Lubricant, 1);
        return Movement.oil;
    }

    @Override
    public IMovement consider() {
        return Movement.oil;
    }
}
