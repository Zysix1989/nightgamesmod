package nightgames.areas;

public class DescriptionModule {

    public static final class ErrorDescriptionModule extends DescriptionModule {

        public ErrorDescriptionModule() {
            super("", "");
        }

        @Override
        public String whereAmI() {
            throw new RuntimeException("This description should never be used!");
        }

        @Override
        public String movedToLocation() {
            throw new RuntimeException("This description should never be used!");
        }
    }

    public static final String QUAD_WHERE_AM_I = "You are in the <b>Quad</b> that sits in the center of the Dorm, the Dining Hall, the Engineering Building, and the Liberal Arts Building. There's "
            + "no one around at this time of night, but the Quad is well-lit and has no real cover. You can probably be spotted from any of the surrounding buildings, it may "
            + "not be a good idea to hang out here for long.";
    public static final String QUAD_MOVED_TO = " head outside, toward the quad.";

    public static DescriptionModule quad() {
        return new DescriptionModule(QUAD_WHERE_AM_I, QUAD_MOVED_TO);
    }

    public static final String DORM_WHERE_AM_I = "You are in the <b>Dorm</b>. Everything is quieter than it would be in any other dorm this time of night. You've been told the entire first floor "
            + "is empty during match hours, but you wouldn't be surprised if a few of the residents are hiding in their rooms, peeking at the fights. You've stashed some clothes "
            + "in one of the rooms you're sure is empty, which is common practice for most of the competitors.";
    public static final String DORM_MOVED_TO = " move to the first floor of the dorm.";

    public static DescriptionModule dorm() {
        return new DescriptionModule(DORM_WHERE_AM_I, DORM_MOVED_TO);
    }

    public static final String SHOWER_WHERE_AM_I = "You are in the first floor <b>Showers</b>. There are a half-dozen stalls shared by the residents on this floor. They aren't very big, but there's "
            + "room to hide if need be. A hot shower would help you recover after a tough fight, but you'd be vulnerable if someone finds you.";
    public static final String SHOWER_MOVED_TO = " run into the showers.";

    public static DescriptionModule shower() {
        return new DescriptionModule(SHOWER_WHERE_AM_I, SHOWER_MOVED_TO);
    }

    public static final String LAUNDRY_WHERE_AM_I = "You are in the <b>Laundry Room</b> in the basement of the Dorm. Late night is prime laundry time in your dorm, but none of these machines "
            + "are running. You're a bit jealous when you notice that the machines here are free, while yours are coin-op. There's a tunnel here that connects to the basement of the "
            + "Dining Hall.";
    public static final String LAUNDRY_MOVED_TO = " move to the laundry room.";
    public static DescriptionModule laundry() {
        return new DescriptionModule(LAUNDRY_WHERE_AM_I, LAUNDRY_MOVED_TO);
    }

    public static final String ENGINEERING_WHERE_AM_I = "You are in the Science and <b>Engineering Building</b>. Most of the lecture rooms are in other buildings; this one is mostly "
            + "for specialized rooms and labs. The first floor contains workshops mostly used by the Mechanical and Electrical Engineering classes. The second floor has "
            + "the Biology and Chemistry Labs. There's a third floor, but that's considered out of bounds.";

    public static final String ENGINEERING_MOVED_TO = " head to the first floor of the engineering building.";
    public static DescriptionModule engineering() {
        return new DescriptionModule(ENGINEERING_WHERE_AM_I, ENGINEERING_MOVED_TO);
    }

    public static final String LAB_WHERE_AM_I = "You are in the <b>Chemistry Lab</b>. The shelves and cabinets are full of all manner of dangerous and/or interesting chemicals. A clever enough "
            + "person could combine some of the safer ones into something useful. Just outside the lab is a bridge connecting to the library.";
    public static final String LAB_MOVED_TO = " enter one of the chemistry labs.";

    public static DescriptionModule lab() {
        return new DescriptionModule(LAB_WHERE_AM_I, LAB_MOVED_TO);
    }

    public static final String WORKSHOP_WHERE_AM_I = "You are in the Mechanical Engineering <b>Workshop</b>. There are shelves of various mechanical components and the back table is covered "
            + "with half-finished projects. A few dozen Mechanical Engineering students use this workshop each week, but it's well stocked enough that no one would miss "
            + "some materials that might be of use to you.";
    public static final String WORKSHOP_MOVED_TO = " enter a workshop.";

    public static DescriptionModule workshop() {
        return new DescriptionModule(WORKSHOP_WHERE_AM_I, WORKSHOP_MOVED_TO);
    }

    public static final String LIBERAL_ARTS_WHERE_AM_I = "You are in the <b>Liberal Arts Building</b>. There are three floors of lecture halls and traditional classrooms, but only "
            + "the first floor is in bounds. The Library is located directly out back, and the side door is just a short walk from the pool.";
    public static final String LIBERAL_ARTS_MOVED_TO = " move to the liberal arts building.";

    public static DescriptionModule liberalArts() {
        return new DescriptionModule(LIBERAL_ARTS_WHERE_AM_I, LIBERAL_ARTS_MOVED_TO);
    }

    public static final String POOL_WHERE_AM_I = "You are by the indoor <b>Pool</b>, which is connected to the Student Union for reasons that no one has ever really explained. The pool here is quite "
            + "large and there is even a jacuzzi. A quick soak would feel good, but the lack of privacy is a concern. The side doors are locked at this time of night, but the "
            + "door to the Student Union is open and there's a back door that exits near the Liberal Arts building. Across the water in the other direction is the Courtyard.";
    public static final String POOL_MOVED_TO = " move to the indoor pool.";

    public static DescriptionModule pool() {
        return new DescriptionModule(POOL_WHERE_AM_I, POOL_MOVED_TO);
    }

    public static final String LIBRARY_WHERE_AM_I = "You are in the <b>Library</b>. It's a two floor building with an open staircase connecting the first and second floors. The front entrance leads to "
            + "the Liberal Arts building. The second floor has a Bridge connecting to the Chemistry Lab in the Science and Engineering building.";
    public static final String LIBRARY_MOVED_TO = " enter the library.";

    public static DescriptionModule library() {
        return new DescriptionModule(LIBRARY_WHERE_AM_I, LIBRARY_MOVED_TO);
    }

    public static final String DINING_HALL_WHERE_AM_I = "You are in the <b>Dining Hall</b>. Most students get their meals here, though some feel it's worth the extra money to eat out. The "
            + "dining hall is quite large and your steps echo on the linoleum, but you could probably find someplace to hide if you need to.";

    public static DescriptionModule diningHall() {
        return new DescriptionModule(DINING_HALL_WHERE_AM_I, DINING_HALL_MOVED_TO);
    }
    public static final String DINING_HALL_MOVED_TO = " head to the dining hall.";

    public static final String KITCHEN_WHERE_AM_I = "You are in the <b>Kitchen</b> where student meals are prepared each day. The industrial fridge and surrounding cabinets are full of the "
            + "ingredients for any sort of bland cafeteria food you can imagine. Fortunately, you aren't very hungry. There's a chance you might be able to cook up some "
            + "of the more obscure items into something useful.";
    public static final String KITCHEN_MOVED_TO = " move into the kitchen.";

    public static DescriptionModule kitchen() {
        return new DescriptionModule(KITCHEN_WHERE_AM_I, KITCHEN_MOVED_TO);
    }

    public static final String STORAGE_WHERE_AM_I = "You are in a <b>Storage Room</b> under the Dining Hall. It's always unlocked and receives a fair bit of foot traffic from students "
            + "using the tunnel to and from the Dorm, so no one keeps anything important in here. There's enough junk down here to provide some hiding places and there's a chance "
            + "you could find something useable in one of these boxes.";
    public static final String STORAGE_MOVED_TO = " enter the storage room.";

    public static DescriptionModule storage() {
        return new DescriptionModule(STORAGE_WHERE_AM_I, STORAGE_MOVED_TO);
    }

    public static final String TUNNEL_WHERE_AM_I = "You are in the <b>Tunnel</b> connecting the dorm to the dining hall. It doesn't get a lot of use during the day and most of the freshmen "
            + "aren't even aware of its existence, but many upperclassmen have been thankful for it on cold winter days and it's proven to be a major tactical asset. The "
            + "tunnel is well-lit and doesn't offer any hiding places.";
    public static final String TUNNEL_MOVED_TO = " move into the tunnel.";

    public static DescriptionModule tunnel() {
        return new DescriptionModule(TUNNEL_WHERE_AM_I, TUNNEL_MOVED_TO);
    }

    public static final String BRIDGE_WHERE_AM_I = "You are on the <b>Bridge</b> connecting the second floors of the Science and Engineering Building and the Library. It's essentially just a "
            + "corridor, so there's no place for anyone to hide.";
    public static final String BRIDGE_MOVED_TO = " move to the bridge.";

    public static DescriptionModule bridge() {
        return new DescriptionModule(BRIDGE_WHERE_AM_I, BRIDGE_MOVED_TO);
    }

    public static final String STUDENT_UNION_WHERE_AM_I = "You are in the <b>Student Union</b>, which doubles as base of operations during match hours. You and the other competitors can pick up "
            + "a change of clothing here.";
    public static final String STUDENT_UNION_MOVED_TO = " head toward the student union.";

    public static DescriptionModule studentUnion() {
        return new DescriptionModule(STUDENT_UNION_WHERE_AM_I, STUDENT_UNION_MOVED_TO);
    }

    public static final String COURTYARD_WHERE_AM_I = "You are in the <b>Courtyard</b>. "
            + "It's a small clearing behind the school pool. There's not much to see here except a tidy garden maintained by the botany department.";
    public static final String COURTYARD_MOVED_TO = " head toward the courtyard.";

    public static DescriptionModule courtyard() {
        return new DescriptionModule(COURTYARD_WHERE_AM_I, COURTYARD_MOVED_TO);
    }

    public static final String POND_WHERE_AM_I = "You are at the edge of a small pond surrounded"
            + " by shrubbery. You could imagine taking a quick dip here, but it's a"
            + " little risky.";
    public static final String POND_MOVED_TO = " wade through the bushes to the pool.";

    public static DescriptionModule pond() {
        return new DescriptionModule(POND_WHERE_AM_I, POND_MOVED_TO);
    }

    public static final String GLADE_WHERE_AM_I = "You are in a glade under a canopy of tall trees. It's"
            + " quite pretty, really. Almost a shame to defile it with the debauchery"
            + " that will inevitably take place here at some point.";
    public static final String GLADE_MOVED_TO = " head into the shaded glade.";

    public static DescriptionModule glade() {
        return new DescriptionModule(GLADE_WHERE_AM_I, GLADE_MOVED_TO);
    }
    public static final String CABIN_WHERE_AM_I = "You are in a small cabin in the woods. There are lots"
            + " of tools here, and if you have the ingredients you could probably make"
            + " some decent traps with them.";
    public static final String CABIN_MOVED_TO = " walk into the cabin.";

    public static DescriptionModule cabin() {
        return new DescriptionModule(CABIN_WHERE_AM_I, CABIN_MOVED_TO);
    }
    public static final String TRAIL_WHERE_AM_I = "You are following a trail along some relatively"
            + " short trees. If you've got the upper body strength, you could"
            + " probably climb up one.";
    public static final String TRAIL_MOVED_TO = " move to the trail.";

    public static DescriptionModule trail() {
        return new DescriptionModule(TRAIL_WHERE_AM_I, TRAIL_MOVED_TO);
    }
    public static final String LODGE_WHERE_AM_I = "You are in a quaint wooden lodge. There are numerous"
            + " herbs and chemicals here, and you should be able to mix up some good"
            + " stuff.";
    public static final String LODGE_MOVED_TO = " head into the lodge.";

    public static DescriptionModule lodge() {
        return new DescriptionModule(LODGE_WHERE_AM_I, LODGE_MOVED_TO);
    }
    public static final String HILL_WHERE_AM_I = "You are on top of a hill overlooking a part of the forest."
            + " If you look closely, you might be able to spot other competitors from here.";
    public static final String HILL_MOVED_TO = " climb up the small hill.";

    public static DescriptionModule hill() {
        return new DescriptionModule(HILL_WHERE_AM_I, HILL_MOVED_TO);
    }
    public static final String PATH_WHERE_AM_I = "You are on a path leading through some bushes. If you can pick"
            + " a good bush to hide in, you might be able to get the drop on passers-by.";
    public static final String PATH_MOVED_TO = " head down the path.";

    public static DescriptionModule path() {
        return new DescriptionModule(PATH_WHERE_AM_I, PATH_MOVED_TO);
    }
    public static final String OAK_WHERE_AM_I = "You are standing under a tall, broad oak. There's something about"
            + " it that somehow resonates inside you. It's quite a comfortable feeling, actually.";
    public static final String OAK_MOVED_TO = " move towards the tall oak.";

    public static DescriptionModule oak() {
        return new DescriptionModule(OAK_WHERE_AM_I, OAK_MOVED_TO);
    }
    public static final String PASS_WHERE_AM_I = "You are walking through a narrow pass carved through a steep"
            + " hill. You could try ambushing someone here, but others could easily do the same"
            + " to you.";
    public static final String PASS_MOVED_TO = " head into the narrow pass.";

    public static DescriptionModule pass() {
        return new DescriptionModule(PASS_WHERE_AM_I, PASS_MOVED_TO);
    }
    public static final String WATERFALL_WHERE_AM_I = "You are next to a pretty waterfall. The river it's in"
            + " bends sharply here, and only this bit is within the bounds for the Games. Still,"
            + " you could use it to take a shower in.";
    public static final String WATERFALL_MOVED_TO = " head to the waterfall.";

    public static DescriptionModule waterfall() {
        return new DescriptionModule(WATERFALL_WHERE_AM_I, WATERFALL_MOVED_TO);
    }
    public static final String MONUMENT_WHERE_AM_I = "You are in an area of the forest dominated by a tall stone"
            + " obelisk. It's probably a monument to something, but there's no plaque to tell you.";
    public static final String MONUMENT_MOVED_TO = " go to the stone monument.";

    public static DescriptionModule monument() {
        return new DescriptionModule(MONUMENT_WHERE_AM_I, MONUMENT_MOVED_TO);
    }
    public static final String DUMP_WHERE_AM_I = "You are at the edge of the forest, where people seem to go to dump"
            + " unwanted trash. The sight disgusts you, but there might be some useful stuff in there.";
    public static final String DUMP_MOVED_TO = " walk to the dumpsite.";

    public static DescriptionModule dump() {
        return new DescriptionModule(DUMP_WHERE_AM_I, DUMP_MOVED_TO);
    }

    private static String baseWhereAmI(String direction) {
        return "You are in a small camp on the " + direction + "ern edge of the forest. ";
    }

    private static String baseMovedTo(String direction) {
        return "head to the " + direction + "base";
    }

    public static DescriptionModule base(String direction) {
        return new DescriptionModule(baseWhereAmI(direction), baseMovedTo(direction));
    }

    private static String campWhereAmI() {
        return "You are in a clearing in the middle of the forest. There are no trees here.";
    }
    public static final String CAMP_MOVED_TO = " head to the central clearing.";

    public static DescriptionModule camp() {
        return new DescriptionModule(campWhereAmI(), CAMP_MOVED_TO);
    }

    private final String whereAmI;
    private final String movedToLocation;

    private DescriptionModule(String whereAmI, String movedToLocation) {
        this.whereAmI = whereAmI;
        this.movedToLocation = movedToLocation;
    }

    public String whereAmI() {
        return whereAmI;
    }

    public String movedToLocation() {
        return movedToLocation;
    }
}
