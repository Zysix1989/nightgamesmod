package nightgames.characters.body;

import com.google.gson.JsonObject;

public abstract class EarPart implements BodyPart {
    public static BodyPart load(JsonObject obj) {
        if (obj.get("enum").getAsString().equals(PointedEarsPart.TYPE)) {
            return new PointedEarsPart();
        }
        if (obj.get("enum").getAsString().equals(CatEarsPart.TYPE)) {
            return new CatEarsPart();
        }
        if (obj.get("enum").getAsString().equals(EarsPart.TYPE)) {
            return new EarsPart();
        }
        throw new IllegalArgumentException("expected an enum field with one of the ear types");
    }

}
