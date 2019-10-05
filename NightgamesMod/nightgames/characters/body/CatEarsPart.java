package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

public class CatEarsPart extends EarPart implements BodyPartMod {
    public static final String TYPE = "cat";

    public CatEarsPart() {
        this.desc = "cat ";
        this.hotness = .15;
        this.pleasure = 1.5;
        this.sensitivity = 1.5;
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("Cute " + fullDescribe(c) + " tops " + c.possessiveAdjective() + " head.");
    }

    @Override
    public String describe(Character c) {
        return desc + "ears";
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
            return 3;
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
