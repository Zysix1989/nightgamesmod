package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.Flatfooted;
import nightgames.status.Horny;

import java.util.Map;
import java.util.Optional;

public class AphrodisiacTrap extends Trap {

    public AphrodisiacTrap() {
        this(null);
    }
    
    public AphrodisiacTrap(Character owner) {
        super("Aphrodisiac Trap", owner);
    }

    public void setStrength(Character user) {
        setStrength(user.get(Attribute.Cunning) + user.get(Attribute.Science) + user.getLevel() / 2);
    }

    @Override
    public void trigger(Participant target) {
        if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
            if (target.getCharacter().human()) {
                Global.gui().message(
                                "You spot a liquid spray trap in time to avoid setting it off. You carefully manage to disarm the trap and pocket the potion.");
                target.getCharacter().gain(Item.Aphrodisiac);
                target.getCharacter().location().remove(this);
            }
        } else {
            if (target.getCharacter().human()) {
                Global.gui().message(
                                "There's a sudden spray of gas in your face and the room seems to get much hotter. Your dick goes rock-hard and you realize you've been "
                                                + "hit with an aphrodisiac.");
            } else if (target.getCharacter().location().humanPresent()) {
                Global.gui().message(
                                target.getCharacter().getName() + " is caught in your trap and sprayed with aphrodisiac. She flushes bright red and presses a hand against her crotch. It seems like "
                                                + "she'll start masturbating even if you don't do anything.");
            }
            target.getCharacter().addNonCombat(new Horny(target.getCharacter(), (30 + getStrength()) / 10, 10, "Aphrodisiac Trap"));
            target.getCharacter().location().opportunity(target.getCharacter(), this);
        }
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Aphrodisiac, 1,
            Item.Tripwire, 1,
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
        return "You set up a spray trap to coat an unwary opponent in powerful aphrodisiac.";
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 12 && !owner.has(Trait.direct);
    }

    @Override
    public Optional<Position> capitalize(Character attacker, Character victim) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        attacker.location().remove(this);
        return super.capitalize(attacker, victim);
    }
}
