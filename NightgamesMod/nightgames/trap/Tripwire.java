package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.stance.StandingOver;
import nightgames.status.Flatfooted;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

public class Tripwire extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Character owner) {
            super(self, owner);
        }

        private static final String VICTIM_TRIGGER_MESSAGE = "You trip over a line of cord and fall on your face.";
        private static final String VICTIM_DISARM_MESSAGE = "You spot a line strung across the corridor " +
                "and carefully step over it.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "{{ victim.subject().defaultNoun() }} carelessly stumbles over the tripwire and lands with an " +
                        "audible thud.");
        private static final JtwigTemplate OWNER_DISARM_TEMPLATE = JtwigTemplate.inlineTemplate(
                "You see {{ victim.object().defaultNoun() }} carefully step over the carefully placed tripwire.");
        @Override
        public void trigger(Participant target) {
            int m = 30 + target.getCharacter().getLevel() * 5;
            if (target.getCharacter().human()) {
                if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                    target.getCharacter().pain(null, null, m);
                    target.getCharacter().location().opportunity(target.getCharacter(), this);
                } else {
                    Global.gui().message(VICTIM_DISARM_MESSAGE);
                    target.getCharacter().location().clearTrap();
                }
            } else {
                if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
                    if (target.getCharacter().location().humanPresent()) {
                        var model = JtwigModel.newModel()
                                .with("victim", target.getCharacter().getGrammar());
                        Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
                    }
                    target.getCharacter().pain(null, null, m);
                    target.getCharacter().location().opportunity(target.getCharacter(), this);
                } else {
                    if (target.getCharacter().location().humanPresent()) {
                        var model = JtwigModel.newModel()
                                .with("victim", target.getCharacter().getGrammar());
                        Global.gui().message(OWNER_DISARM_TEMPLATE.render(model));
                    }
                }
            }
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Flatfooted(victim, 1));
            victim.location().clearTrap();
            return Optional.of(new StandingOver(attacker, victim));
        }
    }
    
    public Tripwire() {
        super("Tripwire");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Rope, 1);

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
        return true;
    }

    private static final String CREATION_MESSAGE = "You run a length of rope at ankle height. It should trip anyone " +
            "who isn't paying much attention.";
}
