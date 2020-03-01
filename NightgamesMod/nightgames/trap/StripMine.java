package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.Flatfooted;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class StripMine extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self, Character owner) {
            super(self, owner);
        }

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                if (target.getCharacter().mostlyNude()) {
                    Global.gui().message(
                            "You're momentarily blinded by a bright flash of light. A camera flash maybe? Is someone taking naked pictures of you?");
                } else {
                    Global.gui().message(
                            "You're suddenly dazzled by a bright flash of light. As you recover from your disorientation, you notice that it feel a bit drafty. "
                                    + "You find you're missing some clothes. You reflect that your clothing expenses have gone up significantly since you joined the Games.");
                }
            } else if (target.getCharacter().location().humanPresent()) {
                Global.gui().message("You're startled by a flash of light not far away. Standing there is a half-naked "
                        + target.getCharacter().getName() + ", looking surprised.");
            }
            IntStream.range(0, 2 + Global.random(4)).forEach(i -> target.getCharacter().shredRandom());
            target.getCharacter().location().opportunity(target.getCharacter(), this);
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            victim.addNonCombat(new Flatfooted(victim, 1));
            attacker.location().remove(this);
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
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Science) >= 4;
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    private static final String CREATION_MESSAGE = "Using the techniques Jett showed you, you rig up a one-time-use " +
            "clothing destruction device.";

}
