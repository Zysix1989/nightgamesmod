package nightgames.characters.body;

import nightgames.characters.body.mods.PartMod;

public class CatTailMod extends PartMod {
    public static final String TYPE = "cat tail";

    public CatTailMod() {
        super(TYPE, .08, 1.5, 1);
    }

    @Override
    public String describeAdjective(String partType) {
        return "feline";
    }
}
