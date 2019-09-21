package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;

public class UseEnergyDrink extends Action {

    public UseEnergyDrink() {
        super("Use " + Item.EnergyDrink);
        name = "Energy Drink";
    }

    @Override
    public boolean usable(Character user) {
        return user.has(Item.EnergyDrink) && !user.bound();
    }

    @Override
    public IMovement execute(Character user) {
        if (user.human()) {
            Global.gui().message(
                "You chug down the unpleasant drink. Your tiredness immediately starts to recede.");
        }
        user.heal(null, 10 + Global.random(10));
        user.consume(Item.EnergyDrink, 1);
        return Movement.enerydrink;
    }

    @Override
    public IMovement consider() {
        return Movement.enerydrink;
    }

}
