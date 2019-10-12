package nightgames.characters.body.mods;

public class SlimeTailMod extends PartMod {
    public static final String TYPE = "slime tail";

    public SlimeTailMod() {
        super(TYPE, 0, 0, 0);
    }

    @Override
    public String describeAdjective(String partType) {
        return "slimy";
    }
}
