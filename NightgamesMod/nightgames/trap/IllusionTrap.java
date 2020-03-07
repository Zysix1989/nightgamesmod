package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Trait;
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

public class IllusionTrap extends Trap {

    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Participant owner) {
            super(self, owner);
            var ch = owner.getCharacter();
            strength = ch.get(Attribute.Arcane) + ch.getLevel() / 2;
        }

        private static final String VICTIM_TRIGGER_MESSAGE = "You run into a girl you don't recognize, but she's " +
                "beautiful and completely naked. You don't have a chance to wonder where she came from, because she " +
                "immediately presses her warm, soft body against you and kisses you passionately. She slips down a " +
                "hand to grope your crotch, and suddenly vanishes after a few strokes. She was just an illusion, but " +
                "your arousal is very real.";

        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "There's a flash of pink light and {{ victim.subject().defaultNoun() }} flushes with arousal.");

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(VICTIM_TRIGGER_MESSAGE);
            } else if (target.getLocation().humanPresent()) {
                var model = JtwigModel.newModel()
                        .with("victim", target.getCharacter().getGrammar());
                Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
            }
            if (target.getCharacter().has(Trait.imagination)) {
                target.getCharacter().tempt(25 + strength);
            }
            target.getCharacter().tempt(25 + strength);
            target.getLocation().opportunity(target.getCharacter(), this);
        }

        @Override
        public Optional<Position> capitalize(Participant attacker, Participant victim) {
            victim.getCharacter().addNonCombat(new Status(new Flatfooted(victim.getCharacter(), 1)));
            victim.getCharacter().location().clearTrap();
            return super.capitalize(attacker, victim);
        }
    }
    
    public IllusionTrap() {
        super("Illusion Trap");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of();

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean recipe(Participant owner) {
        return super.recipe(owner) && owner.getCharacter().canSpend(15);
    }

    @Override
    public boolean requirements(Participant user) {
        return user.getCharacter().get(Attribute.Arcane) >= 5;
    }

    @Override
    protected void deductCostsFrom(Participant c) {
        super.deductCostsFrom(c);
        c.getCharacter().spendMojo(null, 15);
    }

    @Override
    public InstantiateResult instantiate(Participant owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    private static final String CREATION_MESSAGE = "You cast a simple spell that will trigger when someone " +
            "approaches; an illusion will seduce the trespasser.";
}
