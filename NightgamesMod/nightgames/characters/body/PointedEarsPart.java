package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.mods.PartMod;

public class PointedEarsPart extends PartMod {
    public static final String TYPE = "pointed ears";

    public PointedEarsPart() {
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
