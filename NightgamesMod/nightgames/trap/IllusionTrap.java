package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.Flatfooted;

import java.util.Map;
import java.util.Optional;

public class IllusionTrap extends Trap {

    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Character owner) {
            super(self, owner);
        }

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(
                        "You run into a girl you don't recognize, but she's beautiful and completely naked. You don't have a chance to wonder where she came from, because "
                                + "she immediately presses her warm, soft body against you and kisses you passionately. She slips down a hand to grope your crotch, and suddenly vanishes after a few strokes. "
                                + "She was just an illusion, but your arousal is very real.");
            } else if (target.getCharacter().location().humanPresent()) {
                Global.gui().message("There's a flash of pink light and " + target.getCharacter().getName() + " flushes with arousal.");
            }
            if (target.getCharacter().has(Trait.imagination)) {
                target.getCharacter().tempt(25 + getStrength());
            }
            target.getCharacter().tempt(25 + getStrength());
            target.getCharacter().location().opportunity(target.getCharacter(), this);
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Flatfooted(victim, 1));
            victim.location().remove(this);
            return super.capitalize(attacker, victim);
        }

        public void setStrength(Character user) {
            strength = user.get(Attribute.Arcane) + user.getLevel() / 2;
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
    public boolean recipe(Character owner) {
        return super.recipe(owner) && owner.canSpend(15);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Arcane) >= 5;
    }

    @Override
    public String setup(Character owner) {
        basicSetup(owner);
        owner.spendMojo(null, 15);
        return "You cast a simple spell that will trigger when someone approaches; an illusion will seduce the trespasser.";
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        return new InstantiateResult(this.setup(owner), new Instance(this, owner));
    }
}
