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

public class DissolvingTrap extends Trap {

    private static class Instance extends Trap.Instance {
        public Instance(Trap self) {
            super(self);
        }

        @Override
        public void trigger(Participant target, Trap.Instance instance) {
            if (!target.getCharacter().check(Attribute.Perception, 25 + target.getCharacter().baseDisarm())) {
                if (target.getCharacter().human()) {
                    Global.gui().message(
                            "You spot a liquid spray trap in time to avoid setting it off. You carefully manage to disarm the trap and pocket the potion.");
                    target.getCharacter().gain(Item.DisSol);
                    target.getCharacter().location().remove(instance);
                }
            } else {
                if (target.getCharacter().human()) {
                    if (target.getCharacter().reallyNude()) {
                        Global.gui().message(
                                "Your bare foot hits a tripwire and you brace yourself as liquid rains down on you. You hastely do your best to brush the liquid off, "
                                        + "but after about a minute you realize nothing has happened. Maybe the trap was a dud.");
                    } else {
                        Global.gui().message(
                                "You are sprayed with a clear liquid. Everywhere it lands on clothing, it immediately dissolves it, but it does nothing to your skin. "
                                        + "You try valiantly to save enough clothes to preserve your modesty, but you quickly end up naked.");
                    }
                } else if (target.getCharacter().location().humanPresent()) {
                    if (target.getCharacter().reallyNude()) {
                        Global.gui().message(target.getCharacter().getName()
                                + " is caught in your clothes dissolving trap, but she was already naked. Oh well.");
                    } else {
                        Global.gui().message(
                                target.getCharacter().getName() + " is caught in your trap and is showered in dissolving solution. In seconds, her clothes vanish off her body, leaving her "
                                        + "completely nude.");
                    }
                }
                target.getCharacter().nudify();
                target.getCharacter().location().opportunity(target.getCharacter(), instance);
            }
        }
    }

    public DissolvingTrap() {
        this(null);
    }
    
    public DissolvingTrap(Character owner) {
        super("Dissolving Trap", owner);
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Tripwire, 1,
            Item.DisSol, 1,
            Item.Sprayer, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean recipe(Character owner) {
        return super.recipe(owner) && !owner.has(Trait.direct);
    }

    @Override
    public String setup(Character owner) {
        basicSetup(owner);
        return "You rig up a trap to dissolve the clothes of whoever triggers it.";
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 11 && !owner.has(Trait.direct);
    }

    @Override
    public Optional<Position> capitalize(Character attacker, Character victim, Trap.Instance instance) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        return super.capitalize(attacker, victim, instance);
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        return new InstantiateResult(this.setup(owner), new Instance(this));
    }
}
