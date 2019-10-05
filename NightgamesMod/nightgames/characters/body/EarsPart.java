package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

public class EarsPart extends EarPart implements BodyPartMod {
    public static final String TYPE = "normal";

    public EarsPart() {
        this.desc = "normal ";
        this.hotness = 0;
        this.pleasure = 1;
        this.sensitivity = 1;
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
    @Override
    public boolean isNotable() {
        return false;
    }


    @Override
    public int mod(Attribute a, int total) {
        return 0;
    }

    @Override
    public Collection<BodyPartMod> getMods() {
        return Collections.emptySet();
    }

    @Override
    public String getModType() {
        return TYPE;
    }

}
