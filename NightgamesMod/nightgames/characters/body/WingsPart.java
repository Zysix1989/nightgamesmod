package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

public class WingsPart extends GenericBodyPart {
    public static final String TYPE = "wings";

    public WingsPart() {
        super("wings", 1, 1, 1, true, TYPE, "");
    }

    public WingsPart(JsonObject js) {
        super(js);
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("A pair of " + describe(c) + " sits gracefully between " + c.nameOrPossessivePronoun()
                        + " shoulder blades.");
    }

    @Override
    public boolean getDefaultErogenous() {
        return false;
    }

    @Override
    public int attributeModifier(Attribute a) {
        if (a == Attribute.Speed) {
            return 2;
        }
        return 0;
    }

    @Override
    public String adjective() {
        return "alar";
    }

    @Override
    public boolean isMultipleObjects() {
        return true;
    }
}
