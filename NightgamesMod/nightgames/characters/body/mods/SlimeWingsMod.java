package nightgames.characters.body.mods;

public class SlimeWingsMod extends PartMod {
    public static final String TYPE = "slimy wings";

    public SlimeWingsMod() {
        super(TYPE, 0, 0, 1);
    }

    @Override
    public String describeAdjective(String partType) {
        return "slimy";
    }
}
