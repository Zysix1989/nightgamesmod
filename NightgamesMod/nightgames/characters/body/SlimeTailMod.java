package nightgames.characters.body;

import nightgames.characters.body.mods.PartMod;

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
