package nightgames.characters.body.mods;

public class GooeySkinMod extends PartMod {
    public static final String TYPE = "gooey skin";


    public GooeySkinMod() {
        super(TYPE, .5, 1.5, .8);
    }

    @Override
    public String describeAdjective(String partType) {
        return "gooey";
    }
}
