package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;

public class LatexSkinMod extends PartMod {
    public static final String TYPE = "latex";

    public LatexSkinMod() {
        super(TYPE, 3, 1.5, .7);
    }

    @Override
    public String getLongDescriptionOverride(Character self, BodyPart part,
        String previousDescription) {
        return String.format("{self:name-possessive} %s are wrapped in a shiny black material "
            + "that look fused on.",
            super.getLongDescriptionOverride(self, part, previousDescription));
    }

    @Override
    public String describeAdjective(String partType) {
        return "shiny, smooth";
    }
}
