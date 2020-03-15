package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;

public class UseEnergyDrink extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

        @Override
        public String describe(Character c) {
            return " opening an energy drink and downing the whole thing.";
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            user.getCharacter().message("You chug down the unpleasant drink. Your tiredness immediately starts to recede.");
            user.getCharacter().heal(null, 10 + Global.random(10));
            user.getCharacter().consume(Item.EnergyDrink, 1);
            messageOthersInLocation(new Aftermath(user).describe());
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
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
