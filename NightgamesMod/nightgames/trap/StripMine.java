package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Position;
import nightgames.status.Flatfooted;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class StripMine extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Participant owner) {
            super(self, owner);
        }

        private static final String VICTIM_TRIGGER_INEFFECTIVE_MESSAGE = "You're momentarily blinded by a bright " +
                "flash of light. A camera flash maybe? Is someone taking naked pictures of you?";
        private static final String VICTIM_TRIGGER_MESSAGE = "You're suddenly dazzled by a bright flash of light. " +
                "As you recover from your disorientation, you notice that it feel a bit drafty. You find you're " +
                "missing some clothes. You reflect that your clothing expenses have gone up significantly since " +
                "you joined the Games.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "You're startled by a flash of light not far away. Standing there is a naked " +
                        "{{ victim.object().defaultNoun() }}, looking surprised.");
        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                if (target.getCharacter().mostlyNude()) {
                    Global.gui().message(VICTIM_TRIGGER_INEFFECTIVE_MESSAGE);
                } else {
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                }
            } else if (target.getLocation().humanPresent()) {
                var model = JtwigModel.newModel()
                        .with("victim", target.getCharacter().getGrammar());
                Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
            }
            IntStream.range(0, 2 + Global.random(4)).forEach(i -> target.getCharacter().shredRandom());
            target.getLocation().opportunity(target.getCharacter(), this);
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Status(new Flatfooted(victim, 1)));
            attacker.location().clearTrap();
            return super.capitalize(attacker, victim);
        }
    }
    
    public StripMine() {
        super("Strip Mine");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Tripwire, 1, Item.Battery, 3);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Participant user) {
        return user.getCharacter().get(Attribute.Science) >= 4;
    }

    @Override
    public InstantiateResult instantiate(Participant owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    private static final String CREATION_MESSAGE = "Using the techniques Jett showed you, you rig up a one-time-use " +
            "clothing destruction device.";

}
