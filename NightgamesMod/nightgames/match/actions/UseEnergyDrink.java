package nightgames.match.actions;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;

public class UseEnergyDrink extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " opening an energy drink and downing the whole thing.";
        }
    }

    public final class Instance extends Action.Instance {
        private Instance(Action self, Participant user) {
            super(self, user);
        }

        @Override
        public Action.Aftermath execute() {
            return self.execute(user);
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
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You chug down the unpleasant drink. Your tiredness immediately starts to recede.");
        user.getCharacter().heal(null, 10 + Global.random(10));
        user.getCharacter().consume(Item.EnergyDrink, 1);
        return new Aftermath();
    }

}
