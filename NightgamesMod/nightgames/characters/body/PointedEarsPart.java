package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

public class PointedEarsPart extends EarPart implements BodyPartMod {
    public static final String TYPE = "pointed";

    public PointedEarsPart() {
        this.desc = "pointed ";
        this.hotness = .1;
        this.pleasure = 1.2;
        this.sensitivity = 1;
    }

    @Override
    public String describe(Character c) {
            return desc + "ears";
    }

    @Override
    public boolean isReady(Character self) {
        return true;
    }

    @Override public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enum", TYPE);
        return obj;
    }

    @Override
    public boolean isNotable() {
        return true;
    }

    @Override
    public int mod(Attribute a, int total) {
        if (a == Attribute.Seduction) {
                    return 2;
        }
        return 0;
    }

    @Override
    public Collection<BodyPartMod> getMods() {
        return Collections.singleton(this);
    }

    @Override
    public String getModType() {
        return TYPE;
    }

}
