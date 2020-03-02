package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Position;
import nightgames.status.Enthralled;
import nightgames.status.Flatfooted;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;
import java.util.Optional;

// TODO: Rework the text on this trap; it reads weird.
public class EnthrallingTrap extends Trap {
    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Character owner) {
            super(self, owner);
            strength = owner.get(Attribute.Dark) + owner.get(Attribute.Arcane) + owner.getLevel() / 2;
        }

        private static final String VICTIM_AVOID_MESSAGE = "As you walk through the area, you notice a pentagram " +
                "drawn in cum on the floor. Wisely, you avoid stepping into it.";

        private static final String VICTIM_TRIGGER_MESSAGE = "As you're walking, you're suddenly surrounded by " +
                "purple flames. Your mind goes blank for a moment, leaving you staring into the distance. When you " +
                "come back to your senses, you shake your head a few times and hope whatever that thing was, it " +
                "failed at whatever it was supposed to do. The lingering vision of two large red irises staring " +
                "at you suggest differently, though.";

        private static final JtwigTemplate OWNER_TRIGGER_MESSAGE = JtwigTemplate.inlineTemplate(
                "You catch a gout of purple fire in your peripheral vision, but once you have turned to look the " +
                        "flames are gone. All that is left to see is {{ victim.object.defaultNoun() }}, standing still " +
                        "and staring blankly ahead. It would seem to be very easy to have your way with her now, but " +
                        "who or whatever left that thing there will probably be thinking the same."
        );
        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                if (target.getCharacter().check(Attribute.Perception, 25 + target.getCharacter().baseDisarm())
                        || !target.getCharacter().eligible(owner) || !owner.eligible(target.getCharacter())) {
                    Global.gui().message(VICTIM_AVOID_MESSAGE);
                } else {
                    target.getCharacter().location().opportunity(target.getCharacter(), this);
                    Global.gui().message(VICTIM_TRIGGER_MESSAGE);
                    target.getCharacter().addNonCombat(new Status(new Enthralled(target.getCharacter(), owner, 5 + strength / 20)));
                }
            } else if (target.getCharacter().check(Attribute.Perception, 25 + target.getCharacter().baseDisarm()) || !target.getCharacter().eligible(owner) || !owner.eligible(target.getCharacter())) {
                if (target.getCharacter().location().humanPresent()) {
                    var model = JtwigModel.newModel()
                            .with("victim", target.getCharacter().getGrammar());
                    Global.gui().message(OWNER_TRIGGER_MESSAGE.render(model));
                }
                //TODO: Currently, being Enthralled and moving to a new location doesn't use a turn of the effect, meaning that you still lose all those turns once you are in combat.
                target.getCharacter().addNonCombat(new Status(new Enthralled(target.getCharacter(), owner, 5 + strength / 20)));
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

    public EnthrallingTrap() {
        super("Enthralling Trap");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.semen, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Dark) > 5;
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    private static final String CREATION_MESSAGE = "You pop open a bottle of cum and use its contents to draw"
            + " a pentagram on the floor, all the while speaking incantations to cause the first person to step into"
            + " it to be immediately enthralled by you.";
}
