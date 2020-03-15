package nightgames.match.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;

public class Recharge extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " plugging a battery pack into a nearby charging station.";
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
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message("You find a power supply and restore your batteries to full.");
        user.getCharacter().chargeBattery();
        return new Aftermath();
    }

}
