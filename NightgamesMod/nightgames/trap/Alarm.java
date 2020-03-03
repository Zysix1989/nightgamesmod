package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;

public class Alarm extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Participant owner) {
            super(self, owner);
        }

        private static final String VICTIM_TRIGGER_MESSAGE = "You're walking through the eerily quiet campus, when a loud " +
                "beeping almost makes you jump out of your skin. You realize the beeping is coming from a cell " +
                "phone on the floor. You shut it off as quickly as you can, but it's likely everyone nearby heard " +
                "it already.";

        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "{{ victim.subject().defaultNoun() }} sets off your alarm, giving away {{ victim.possessiveAdjective() }} presence."
        );

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(VICTIM_TRIGGER_MESSAGE);
            } else if (target.getCharacter().location().humanPresent()) {
                var model = JtwigModel.newModel()
                        .with("victim", target.getCharacter().getGrammar());
                Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
            }
            target.getCharacter().location().alarm = true;
            target.getCharacter().location().clearTrap();
        }
    }

    public Alarm() {
        super("Alarm");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Tripwire, 1,
            Item.Phone, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public InstantiateResult instantiate(Participant owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    @Override
    public boolean requirements(Participant user) {
        return user.getCharacter().get(Attribute.Cunning) >= 6;
    }

    private static final String CREATION_MESSAGE = "You rig up a disposable phone to a tripwire. When someone trips " +
            "the wire, it should set of the phone's alarm.";
}
