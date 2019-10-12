package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.body.mods.CatEarsMod;
import nightgames.characters.body.mods.PointedEarsMod;
import nightgames.global.Global;

public class EarsPart extends GenericBodyPart {
    public static final String TYPE = "ears";
    private static final String NORMAL_TYPE = "normal";

    public static BodyPart load(JsonObject obj) {
        if (obj.get("enum").getAsString().equals(PointedEarsMod.TYPE)) {
            var ears = new EarsPart();
            ears.addMod(new PointedEarsMod());
            return ears;
        }
        if (obj.get("enum").getAsString().equals(CatEarsMod.TYPE)) {
            var ears = new EarsPart();
            ears.addMod(new CatEarsMod());
            return ears;
        }
        if (obj.get("enum").getAsString().equals(NORMAL_TYPE)) {
            return new EarsPart();
        }
        throw new IllegalArgumentException("expected an enum field with one of the ear types");
    }

    public EarsPart() {
        super("normal ", 0, 1, 1, TYPE, "");
    }

    @Override
    public String canonicalDescription() {
        return desc + "ears";
    }

    @Override
    public double priority(Character c) {
        return getPleasure(c);
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append(Global.capitalizeFirstLetter(fullDescribe(c)))
            .append(" frames ")
            .append(c.possessiveAdjective())
            .append(" face.");
    }

    @Override
    public String adjective() {
        return "otic";
    }

    @Override public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enum", getMods().stream().map(m -> m.getModType()).findAny().orElse(NORMAL_TYPE));
        return obj;
    }
}
