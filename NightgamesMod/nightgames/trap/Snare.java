package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Position;
import nightgames.status.Bound;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

public class Snare extends Trap {
    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Character owner) {
            super(self, owner);
            strength = owner.get(Attribute.Cunning) + owner.getLevel() / 2;
        }

        private static final String VICTIM_DISARM_MESSAGE = "You notice a snare on the floor in front of you and " +
                "manage to disarm it safely";
        private static final String VICTIM_TRIGGER_MESSAGE = "You hear a sudden snap and you're suddenly overwhelmed " +
                "by a blur of ropes. The tangle of ropes trip you up and firmly bind your arms.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "{{ victim.subject().defaultNoun() }} enters the room, sets off your snare, and ends up thoroughly " +
                        "tangled in rope.");
        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().check(Attribute.Perception, 25 + strength + target.getCharacter().baseDisarm())) {
                if (target.getCharacter().human()) {
                    Global.gui().message(VICTIM_DISARM_MESSAGE);
                }
                target.getCharacter().location().clearTrap();
            } else {
                target.getCharacter().addNonCombat(new Status(new Bound(target.getCharacter(), 30 + strength / 2.0f, "snare")));
                if (target.getCharacter().human()) {
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                } else if (target.getCharacter().location().humanPresent()) {
                    var model = JtwigModel.newModel()
                            .with("victim", target.getCharacter().getGrammar());
                    Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
                }
                target.getCharacter().location().opportunity(target.getCharacter(), this);
            }
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            attacker.location().clearTrap();
            return super.capitalize(attacker, victim);
        }
    }

    public Snare() {
        super("Snare");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Tripwire, 1, Item.Rope, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }
    
    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 9;
    }

    private static final String CREATION_MESSAGE = "You carefully rig up a complex and delicate system of ropes on a " +
            "tripwire. In theory, it should be able to bind whoever triggers it.";
}
