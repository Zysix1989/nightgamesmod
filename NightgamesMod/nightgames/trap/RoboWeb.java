package nightgames.trap;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.RoboWebbed;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

public class RoboWeb extends Trap {
    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Character owner) {
            super(self, owner);
            strength = owner.getLevel();
        }

        private static String victimTriggerMessage(Participant target) {
            var msg = new StringBuilder();
            msg.append("The instant you feel your foot catch on a tripwire, you know something"
                    + " terrible is about to happen. Your instincts prove correct as ropes come flying"
                    + " out of every corner, straight at you. The intricate contraption suspends"
                    + " you above the ground, leaving all but your head hopelessly tied up."
                    + " You look around and see that it's not actually rope which has so"
                    + " thoroughly bound you; it looks more like webbing. ");
            if (!target.getCharacter().mostlyNude()) {
                msg.append("Whatever it is, your clothing is not reacting well to it. Wherever"
                        + " it touches the strange material, it melts away, although your skin"
                        + " is mercifully unaffected. What disjointed scraps of your clothes remain"
                        + " fall to the floor in a sad heap. ");
            }
            msg.append("The strands of the web start vibrating softly, caressing every bit of skin"
                    + " they touch. Which is pretty much all of it. The webbing around your ");
            if (target.getCharacter().hasDick()) {
                msg.append(target.getCharacter().body.getRandomCock().describe(target.getCharacter()));
            } else if (target.getCharacter().hasPussy()) {
                msg.append(target.getCharacter().body.getRandomPussy().describe(target.getCharacter()));
            } else {
                msg.append("sensitive nipples");
            }
            msg.append(" are especially distracting, as they drive you right to the edge of orgasm.");
            return msg.toString();
        }

        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "You hear a loud <i>SNAP</i> coming from nearby. Looking around, you see a mess of rope-like cords " +
                        "flying towards you. You duck out of the way, but it seems the cords were not meant to hit " +
                        "you in the first place. Instead, they and many others like them have ensnared " +
                        "{{ victim.object().defaultNoun() }}, hoisting {{ victim.object().pronoun() }} into the air " +
                        "and leaving {{ victim.object().pronoun() }} completely immobile. The clothes " +
                        "{{ victim.subject().pronoun() }} was wearing disappear from beneath the web-like structure, " +
                        "and {{ victim.subject().pronoun() }} thrashes around wildly, moaning loudly. " +
                        "{{ victim.subject().defaultNoun() }} is not getting out of there anytime soon. Oh, the " +
                        "possibilities...");

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(victimTriggerMessage(target));
            } else {
                var model = JtwigModel.newModel()
                        .with("victim", target.getCharacter().getGrammar());
                Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
            }
            target.getCharacter().outfit.undress();
            target.getCharacter().addNonCombat(new RoboWebbed(target.getCharacter(), 100 + strength, this));
            target.getCharacter().location().opportunity(target.getCharacter(), this);
            target.getCharacter().location().alarm = true;
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            attacker.location().clearTrap();
            return super.capitalize(attacker, victim);
        }

    }
    
    public RoboWeb() {
        super("RoboWeb");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Rope, 4,
            Item.Spring, 2,
            Item.Tripwire, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.has(Trait.roboweb);
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult("<invisible>", new Instance(this, owner));
    }
}
