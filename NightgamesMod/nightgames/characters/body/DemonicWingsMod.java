package nightgames.characters.body;

import nightgames.characters.body.mods.PartMod;

public class DemonicWingsMod extends PartMod {
    public static final String TYPE = "demonic wings";

    public DemonicWingsMod() {
        super(TYPE, .1, 1.3, 1.2);
    }

    @Override
    public String describeAdjective(String partType) {
        return "demonic";
    }
}
