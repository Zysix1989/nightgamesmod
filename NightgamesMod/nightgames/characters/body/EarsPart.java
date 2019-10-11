package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

public class EarsPart extends EarPart {
    public static final String TYPE = "normal";

    public EarsPart() {
        super("normal ", 0, 1, 1);
    }

    @Override
    public String describe(Character c) {
        return "ears";
    }

    @Override public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enum", TYPE);
        return obj;
    }
}
