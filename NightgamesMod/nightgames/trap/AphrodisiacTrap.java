package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Position;
import nightgames.status.Flatfooted;
import nightgames.status.Horny;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

public class AphrodisiacTrap extends Trap {
    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Character owner) {
            super(self, owner);
            strength = owner.get(Attribute.Cunning) + owner.get(Attribute.Science) + owner.getLevel() / 2;
        }

        private static final String VICTIM_DISARM_MESSAGE = "You spot a liquid spray trap in time to avoid setting " +
                "it off. You carefully manage to disarm the trap and pocket the potion.";
        private static final String VICTIM_TRIGGER_MESSAGE = "There's a sudden spray of gas in your face and the room " +
                "seems to get much hotter. Your dick goes rock-hard and you realize you've been hit with an " +
                "aphrodisiac.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "You watch {{ victim.subject() }} get blasted with aphrodisiac from your trap. " +
                        "{{ victim.subject().pronoun() }} flushes bright red and presses a hand against " +
                        "{{ victim.possessiveAdjective() }} crotch. It seems like {{ victim.subject().pronoun() }}'ll " +
                        "start masturbating even if you don't do anything.");

        @Override
        public void trigger(Participant target) {
            if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
                if (target.getCharacter().human()) {
                    Global.gui().message(VICTIM_DISARM_MESSAGE);
                    target.getCharacter().gain(Item.Aphrodisiac);
                    target.getCharacter().location().clearTrap();
                }
            } else {
                if (target.getCharacter().human()) {
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                } else if (target.getCharacter().location().humanPresent()) {
                    var model = JtwigModel.newModel()
                            .with("victim", target.getCharacter().getGrammar());
                    Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
                }
                target.getCharacter().addNonCombat(new Status(new Horny(target.getCharacter(), (30 + strength) / 10.0f, 10, "Aphrodisiac Trap")));
                target.getCharacter().location().opportunity(target.getCharacter(), this);
            }
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Status(new Flatfooted(victim, 1)));
            attacker.location().clearTrap();
            return super.capitalize(attacker, victim);
        }
    }
    
    public AphrodisiacTrap() {
        super("Aphrodisiac Trap");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Aphrodisiac, 1,
            Item.Tripwire, 1,
            Item.Sprayer, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean recipe(Character owner) {
        return super.recipe(owner);
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 12 && !owner.has(Trait.direct);
    }

    private static final String CREATION_MESSAGE = "You set up a spray trap to coat an unwary opponent in powerful " +
            "aphrodisiac.";
}
