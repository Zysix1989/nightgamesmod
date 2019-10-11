package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.body.mods.CatEarsMod;
import nightgames.characters.body.mods.PointedEarsMod;
import nightgames.global.Global;

public abstract class EarPart extends GenericBodyPart {
    public static final String TYPE = "ears";

    public String desc;
    public double hotness;
    public double pleasure;
    public double sensitivity;

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
        if (obj.get("enum").getAsString().equals(EarsPart.TYPE)) {
            return new EarsPart();
        }
        throw new IllegalArgumentException("expected an enum field with one of the ear types");
    }

    protected EarPart(String desc, double hotness, double pleasure, double sensitivity) {
        super(desc, hotness, pleasure, sensitivity, TYPE, "");
    }

    @Override
    public String canonicalDescription() {
        return desc + "ears";
    }

    @Override
    public double priority(Character c) {
        return getPleasure(c, null);
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
}
