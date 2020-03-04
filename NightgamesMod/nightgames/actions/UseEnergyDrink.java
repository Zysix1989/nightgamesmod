package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;

public class UseEnergyDrink extends Action {

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return Movement.enerydrink.describe(c);
        }
    }

    public UseEnergyDrink() {
        super("Energy Drink");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().human()) {
            Global.gui().message(
                "You chug down the unpleasant drink. Your tiredness immediately starts to recede.");
        }
        user.getCharacter().heal(null, 10 + Global.random(10));
        user.getCharacter().consume(Item.EnergyDrink, 1);
        return new Aftermath();
    }

}
