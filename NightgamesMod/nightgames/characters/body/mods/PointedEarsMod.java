package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Attribute;

public class PointedEarsMod extends PartMod {
    public static final String TYPE = "pointed ears";

    public PointedEarsMod() {
        super(TYPE, .1, .2, 0);
    }

    @Override
    public String describeAdjective(String partType) {
            return "pointed";
    }

    @Override
    public Optional<Integer> attributeModifier(Attribute a) {
        if (a == Attribute.Seduction) {
            return Optional.of(2);
        }
        return Optional.empty();
    }
}
