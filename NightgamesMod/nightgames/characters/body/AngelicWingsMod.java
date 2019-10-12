package nightgames.characters.body;

import nightgames.characters.body.mods.PartMod;

public class AngelicWingsMod extends PartMod {
    public static final String TYPE = "angelic wings";

    public AngelicWingsMod() {
        this(TYPE);
    }

    protected AngelicWingsMod(String modType) {
        super(modType, .1, 1.4, 1.2);
    }

    @Override
    public String describeAdjective(String partType) {
        return null;
    }
}
