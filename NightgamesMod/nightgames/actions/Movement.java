package nightgames.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;

public enum Movement implements IMovement {
    // All of the movement strings are appended to 'You notice NAME', to produce
    // a line like 'You notice Cassie move to the indoor pool."
    quad(" head outside, toward the quad."),
    kitchen(" move into the kitchen."),
    dorm(" move to the first floor of the dorm."),
    shower(" run into the showers."),
    storage(" enter the storage room."),
    dining(" head to the dining hall."),
    laundry(" move to the laundry room."),
    tunnel(" move into the tunnel."),
    bridge(" move to the bridge."),
    engineering(" head to the first floor of the engineering building."),
    workshop(" enter a workshop."),
    lab(" enter one of the chemistry labs."),
    la(" move to the liberal arts building."),
    library(" enter the library."),
    pool(" move to the indoor pool."),
    union(" head toward the student union."),
    courtyard(" head toward the courtyard."),
    hide(" disappear into a hiding place."),
    trap(" start rigging up something weird, probably a trap."),
    bathe(" start bathing in the nude, not bothered by your presence."),
    scavenge(" begin scrounging through some boxes in the corner."),
    craft(" start mixing various liquids. Whatever it is doesn't look healthy."),
    wait(" loitering nearby"),
    struggle((Character n) -> String.format(" is struggling against %s bondage.", n.possessiveAdjective())),
    resupply(" heads for one of the safe rooms, probably to get a change of clothes."),
    oil((Character n) -> String.format(" rubbing body oil on every inch of %s skin. Wow, you wouldn't mind watching that again.", n.possessiveAdjective())),
    enerydrink(" opening an energy drink and downing the whole thing."),
    beer(" opening a beer and downing the whole thing."),
    recharge(" plugging a battery pack into a nearby charging station."),
    locating((Character n) -> String.format(" is holding someone's underwear in %s hands and breathing deeply. Strange.", n.possessiveAdjective())),
    mana((Character n) -> String.format(" doing something with a large book. When %s's finished, you can see a sort of aura coming from %s.", n.pronoun(), n.possessiveAdjective())),
    retire(" has left the match."),
    ftcNorthBase(" head to the north camp."),
    ftcWestBase(" move to the west camp."),
    ftcSouthBase(" go to the south camp."),
    ftcEastBase(" walk to the east camp."),
    ftcCenter(" head to the central clearing."),
    ftcPond(" wade through the bushes to the pool."),
    ftcGlade(" head into the shaded glade."),
    ftcCabin(" walk into the cabin."),
    ftcTrail(" move to the trail."),
    ftcLodge(" head into the lodge."),
    ftcHill(" climb up the small hill."),
    ftcPath(" head down the path."),
    ftcOak(" move towards the tall oak."),
    ftcPass(" head into the narrow pass."),
    ftcWaterfall(" head to the waterfall."),
    ftcMonument(" go to the stone monument."),
    ftcDump(" walk to the dumpsite."),
    ftcTreeAmbush(" climb up a tree."),
    ftcBushAmbush(" dive into some bushes."),
    ftcPassAmbush(" slip into an alcove."),
    disguise(" shimmer and turn into someone else!"),
    masturbate((Character n) -> {
        String mast;
        if (n.hasDick()) {
            mast = String.format(" starts to stroke %s cock ", n.possessiveAdjective());
        } else if (n.hasPussy()) {
            mast = String.format(" starts to stroke %s pussy ", n.possessiveAdjective());
        } else {
            mast = String.format(" starts to finger %s ass ", n.possessiveAdjective());
        }
        return mast + "while trying not to make much noise. It's quite a show.";
    });
    
    private interface DescriptionProducer {
        public String getDescriptionFor(Character doer);
    }
    private DescriptionProducer producer;

    /**
     * @return the Item name
     */
    public String describe(Character doer) {
        return producer.getDescriptionFor(doer);
    }

    private Movement(String desc) {
        this.producer = n -> desc;
    }
    
    private Movement(DescriptionProducer producer) {
        this.producer = producer;
    }

    public static Movement ftcBaseMovement(Area base) {
        switch (base.name) {
            case "North Base":
                return ftcNorthBase;
            case "East Base":
                return ftcEastBase;
            case "South Base":
                return ftcSouthBase;
            case "West Base":
                return ftcWestBase;
            case "Central Camp":
                return ftcCenter;
            default:
                throw new IllegalArgumentException("Not an FTC base: " + base.name);
        }
    }
}
