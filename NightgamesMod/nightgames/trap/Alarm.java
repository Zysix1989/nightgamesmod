package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;

import java.util.Map;

public class Alarm extends Trap {

    public Alarm() {
        this(null);
    }

    public Alarm(Character owner) {
        super("Alarm", owner);
    }

    @Override
    public void trigger(Participant target) {
        if (target.getCharacter().human()) {
            Global.gui().message(
                            "You're walking through the eerily quiet campus, when a loud beeping almost makes you jump out of your skin. You realize the beeping is "
                                            + "coming from a cell phone on the floor. You shut it off as quickly as you can, but it's likely everyone nearby heard it already.");
        } else if (target.getCharacter().location().humanPresent()) {
            Global.gui().message(target.getCharacter().getName() + " Sets off your alarm, giving away her presence.");
        }
        target.getCharacter().location().alarm = true;
        target.getCharacter().location().remove(this);
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Tripwire, 1,
            Item.Phone, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public String setup(Character user) {
        basicSetup(user);
        return "You rig up a disposable phone to a tripwire. When someone trips the wire, it should set of the phone's alarm.";
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 6;
    }

}
