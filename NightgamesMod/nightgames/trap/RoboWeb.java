package nightgames.trap;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.RoboWebbed;

import java.util.Map;
import java.util.Optional;

public class RoboWeb extends Trap {

    public RoboWeb() {
        this(null);
    }
    
    public RoboWeb(Character owner) {
        super("RoboWeb", owner);
    }

    @Override
    public void trigger(Participant target, Instance instance) {
        if (target.getCharacter().human()) {
            String msg = "";
            msg += "The instant you feel your foot catch on a tripwire, you know something"
                            + " terrible is about to happen. Your instincts prove correct as ropes come flying"
                            + " out of every corner, straight at you. The intricate contraption suspends"
                            + " you above the ground, leaving all but your head hopelessly tied up."
                            + " You look around and see that it's not actually rope which has so"
                            + " thoroughly bound you; it looks more like webbing. ";
            if (!target.getCharacter().mostlyNude()) {
                msg +="Whatever it is, your clothing is not reacting well to it. Wherever"
                                + " it touches the strange material, it melts away, although your skin"
                                + " is mercifully unaffected. What disjointed scraps of your clothes remain"
                                + " fall to the floor in a sad heap. ";
            }
            msg += "The strands of the web start vibrating softly, caressing every bit of skin"
                            + " they touch. Which is pretty much all of it. The webbing around your ";
            if (target.getCharacter().hasDick()) {
                msg += target.getCharacter().body.getRandomCock().describe(target.getCharacter());
            } else if (target.getCharacter().hasPussy()) {
                msg += target.getCharacter().body.getRandomPussy().describe(target.getCharacter());
            } else {
                msg += "sensitive nipples";
            }
            msg += " are especially distracting, as they drive you right to the edge of orgasm.";
            Global.gui().message(msg);
        } else {
            Global.gui().message(String.format("You hear a loud <i>SNAP</i> coming from nearby. Looking around, you"
                            + " see a mess of rope-like cords flying towards you. You duck out of the way,"
                            + " but it seems the cords were not meant to hit you in the first place."
                            + " Instead, they and many others like them have ensnared %s, hoisting"
                            + " %s into the air and leaving %s completely immobile. The clothes %s"
                            + " was wearing disappear from beneath the web-like structure, and %s"
                            + " thrashes around wildly, moaning loudly. %s is not getting"
                            + " out of there anytime soon. Oh, the possibilities...", target.getCharacter().getName(), target.getCharacter().objectPronoun(),
                            target.getCharacter().objectPronoun(), target.getCharacter().pronoun(), target.getCharacter().pronoun(),
                            Global.capitalizeFirstLetter(target.getCharacter().pronoun())));
        }
        target.getCharacter().outfit.undress();
        target.getCharacter().addNonCombat(new RoboWebbed(target.getCharacter(), 100 + getStrength(), instance));
        target.getCharacter().location().opportunity(target.getCharacter(), instance);
        target.getCharacter().location().alarm = true;
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
    public String setup(Character owner) {
        basicSetup(owner);
        return "<invisible>";
    }
    
    @Override
    public Optional<Position> capitalize(Character attacker, Character victim, Instance instance) {
        attacker.location().remove(instance);
        return super.capitalize(attacker, victim, instance);
    }

}
