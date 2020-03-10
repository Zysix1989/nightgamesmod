package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Participant;

public class Recharge extends Action {
    private static final long serialVersionUID = 2089054062272510717L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " plugging a battery pack into a nearby charging station.";
        }
    }

    public Recharge() {
        super("Recharge");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().get(Attribute.Science) > 0
                && user.getCharacter().count(Item.Battery) < 20
                && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You find a power supply and restore your batteries to full.");
        user.getCharacter().chargeBattery();
        return new Aftermath();
    }

}
