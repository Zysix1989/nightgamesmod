package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;

public class Recharge extends Action {

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            user.getCharacter().message("You find a power supply and restore your batteries to full.");
            user.getCharacter().chargeBattery();
            messageOthersInLocation(user.getCharacter().getGrammar().subject().defaultNoun() +
                    " plugging a battery pack into a nearby charging station.");
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
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
