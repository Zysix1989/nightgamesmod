package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTrait;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.stance.StandingOver;
import nightgames.status.Flatfooted;
import nightgames.status.Winded;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

public class SpringTrap extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Character owner) {
            super(self, owner);
        }

        private static final String VICTIM_DISARM_MESSAGE = "You spot a suspicious mechanism on the floor and prod it " +
                "from a safe distance. A spring loaded line shoots up to groin height, which would have been very " +
                "unpleasant if you had kept walking.";
        private static final String VICTIM_TRIGGER_MESSAGE = "As you're walking, your foot hits something and " +
                "there's a sudden debilitating pain in your groin. Someone has set up a spring-loaded rope designed " +
                "to shoot up into your nuts, which is what just happened. You collapse into the fetal position and " +
                "pray that there's no one nearby.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "You hear a sudden yelp as your trap catches {{ victim.object().defaultNoun() }} right in the cooch. " +
                        "She eventually manages to extract the rope from between her legs and collapses to the floor " +
                        "in pain.");

        @Override
        public void trigger(Participant target) {
            if (!target.getCharacter().check(Attribute.Perception, 24 - target.getCharacter().get(Attribute.Perception) + target.getCharacter().baseDisarm())) {
                if (target.getCharacter().human()) {
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                } else if (target.getCharacter().location().humanPresent()) {
                    var model = JtwigModel.newModel()
                            .with("victim", target.getCharacter().getGrammar());
                    Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
                }
                int m = 50 + target.getCharacter().getLevel() * 5;
                if (target.getCharacter().has(ClothingTrait.armored)) {
                    m /= 2;
                    target.getCharacter().pain(null, null, m);
                } else {
                    if (target.getCharacter().has(Trait.achilles)) {
                        m += 20;
                    }
                    target.getCharacter().pain(null, null, m);
                    target.getCharacter().addNonCombat(new Winded(target.getCharacter()));
                }
                target.getCharacter().location().opportunity(target.getCharacter(), this);
            } else if (target.getCharacter().human()) {
                Global.gui().message(VICTIM_DISARM_MESSAGE);
                target.getCharacter().location().remove(this);
            }
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Flatfooted(victim, 1));
            attacker.location().remove(this);
            return Optional.of(new StandingOver(attacker, victim));
        }
    }
    
    public SpringTrap() {
        super("Spring Trap");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Spring, 1, Item.Rope, 1);

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
        return owner.get(Attribute.Cunning) >= 10;
    }

    private static final String CREATION_MESSAGE = "You manage to rig up a makeshift booby trap, which should prove " +
            "quite unpleasant to any who stumbles upon it.";
}
