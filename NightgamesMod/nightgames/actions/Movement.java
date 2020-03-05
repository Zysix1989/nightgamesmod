package nightgames.actions;

import nightgames.characters.Character;

public enum Movement implements IMovement {
    // All of the movement strings are appended to 'You notice NAME', to produce
    // a line like 'You notice Cassie move to the indoor pool."
    quad(Constants.QUAD_MOVED_TO),
    kitchen(Constants.KITCHEN_MOVED_TO),
    dorm(Constants.DORM_MOVED_TO),
    shower(Constants.SHOWER_MOVED_TO),
    storage(Constants.STORAGE_MOVED_TO),
    dining(Constants.DINING_HALL_MOVED_TO),
    laundry(Constants.LAUNDRY_MOVED_TO),
    tunnel(Constants.TUNNEL_MOVED_TO),
    bridge(Constants.BRIDGE_MOVED_TO),
    engineering(Constants.ENGINEERING_MOVED_TO),
    workshop(Constants.WORKSHOP_MOVED_TO),
    lab(Constants.LAB_MOVED_TO),
    la(Constants.LIBERAL_ARTS_MOVED_TO),
    library(Constants.LIBRARY_MOVED_TO),
    pool(Constants.POOL_MOVED_TO),
    union(Constants.STUDENT_UNION_MOVED_TO),
    courtyard(Constants.COURTYARD_MOVED_TO),
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
    ftcCenter(Constants.CAMP_MOVED_TO),
    ftcPond(Constants.POND_MOVED_TO),
    ftcGlade(Constants.GLADE_MOVED_TO),
    ftcCabin(Constants.CABIN_MOVED_TO),
    ftcTrail(Constants.TRAIL_MOVED_TO),
    ftcLodge(Constants.LODGE_MOVED_TO),
    ftcHill(Constants.HILL_MOVED_TO),
    ftcPath(Constants.PATH_MOVED_TO),
    ftcOak(Constants.OAK_MOVED_TO),
    ftcPass(Constants.PASS_MOVED_TO),
    ftcWaterfall(Constants.WATERFALL_MOVED_TO),
    ftcMonument(Constants.MONUMENT_MOVED_TO),
    ftcDump(Constants.DUMPSITE_MOVED_TO),
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
        String getDescriptionFor(Character doer);
    }
    private DescriptionProducer producer;

    /**
     * @return the Item name
     */
    public String describe(Character doer) {
        return producer.getDescriptionFor(doer);
    }

    Movement(String desc) {
        this.producer = n -> desc;
    }
    
    Movement(DescriptionProducer producer) {
        this.producer = producer;
    }

    private static class Constants {
        public static final String QUAD_MOVED_TO = " head outside, toward the quad.";
        public static final String KITCHEN_MOVED_TO = " move into the kitchen.";
        public static final String DORM_MOVED_TO = " move to the first floor of the dorm.";
        public static final String SHOWER_MOVED_TO = " run into the showers.";
        public static final String STORAGE_MOVED_TO = " enter the storage room.";
        public static final String DINING_HALL_MOVED_TO = " head to the dining hall.";
        public static final String LAUNDRY_MOVED_TO = " move to the laundry room.";
        public static final String TUNNEL_MOVED_TO = " move into the tunnel.";
        public static final String BRIDGE_MOVED_TO = " move to the bridge.";
        public static final String ENGINEERING_MOVED_TO = " head to the first floor of the engineering building.";
        public static final String WORKSHOP_MOVED_TO = " enter a workshop.";
        public static final String LAB_MOVED_TO = " enter one of the chemistry labs.";
        public static final String LIBERAL_ARTS_MOVED_TO = " move to the liberal arts building.";
        public static final String LIBRARY_MOVED_TO = " enter the library.";
        public static final String POOL_MOVED_TO = " move to the indoor pool.";
        public static final String STUDENT_UNION_MOVED_TO = " head toward the student union.";
        public static final String COURTYARD_MOVED_TO = " head toward the courtyard.";
        public static final String CAMP_MOVED_TO = " head to the central clearing.";
        public static final String POND_MOVED_TO = " wade through the bushes to the pool.";
        public static final String GLADE_MOVED_TO = " head into the shaded glade.";
        public static final String CABIN_MOVED_TO = " walk into the cabin.";
        public static final String TRAIL_MOVED_TO = " move to the trail.";
        public static final String LODGE_MOVED_TO = " head into the lodge.";
        public static final String HILL_MOVED_TO = " climb up the small hill.";
        public static final String PATH_MOVED_TO = " head down the path.";
        public static final String OAK_MOVED_TO = " move towards the tall oak.";
        public static final String PASS_MOVED_TO = " head into the narrow pass.";
        public static final String WATERFALL_MOVED_TO = " head to the waterfall.";
        public static final String MONUMENT_MOVED_TO = " go to the stone monument.";
        public static final String DUMPSITE_MOVED_TO = " walk to the dumpsite.";
    }
}
