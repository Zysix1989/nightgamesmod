package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.stance.StandingOver;
import nightgames.status.Flatfooted;

import java.util.Map;
import java.util.Optional;

public class Tripline extends Trap {
    
    public Tripline() {
        this(null);
    }
    
    public Tripline(Character owner) {
        super("Tripline", owner);
    }

    @Override
    public void trigger(Participant target) {
        int m = 30 + target.getCharacter().getLevel() * 5;
        if (target.getCharacter().human()) {
            if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
                Global.gui().message("You trip over a line of cord and fall on your face.");
                target.getCharacter().pain(null, null, m);
                target.getCharacter().location().opportunity(target.getCharacter(), this);
            } else {
                Global.gui().message("You spot a line strung across the corridor and carefully step over it.");
                target.getCharacter().location().remove(this);
            }
        } else {
            if (!target.getCharacter().check(Attribute.Perception, 20 + target.getCharacter().baseDisarm())) {
                if (target.getCharacter().location().humanPresent()) {
                    Global.gui().message(target.getCharacter().getName()
                                    + " carelessly stumbles over the tripwire and lands with an audible thud.");
                }
                target.getCharacter().pain(null, null, m);
                target.getCharacter().location().opportunity(target.getCharacter(), this);
            } else {
                if (target.getCharacter().location().humanPresent()) {
                    Global.gui().message("You see " + target.getCharacter().getName() + " carefully step over the carefully placed tripline." );
                }
            }
        }
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Rope, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public String setup(Character owner) {
        basicSetup(owner);
        return "You run a length of rope at ankle height. It should trip anyone who isn't paying much attention.";
    }

    @Override
    public boolean requirements(Character owner) {
        return true;
    }

    @Override
    public Optional<Position> capitalize(Character attacker, Character victim) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        victim.location().remove(this);
        return Optional.of(new StandingOver(attacker, victim));
    }

}
