package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;

public abstract class EarPart extends GenericBodyPart {
    public static final String TYPE = "ears";

    public String desc;
    public double hotness;
    public double pleasure;
    public double sensitivity;

    public static BodyPart load(JsonObject obj) {
        if (obj.get("enum").getAsString().equals(PointedEarsPart.TYPE)) {
            var ears = new EarsPart();
            ears.addMod(new PointedEarsPart());
            return ears;
        }
        if (obj.get("enum").getAsString().equals(CatEarsPart.TYPE)) {
            var ears = new EarsPart();
            ears.addMod(new CatEarsPart());
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
