package nightgames.characters.body;

import com.google.gson.JsonObject;

public class StraponPart extends GenericBodyPart {
    public static final String TYPE = "strapon";

    public StraponPart() {
        super("strap-on", 1, 1, -999, TYPE, "a ");
    }

    public StraponPart(JsonObject js) {
        super(js);
    }

    protected StraponPart(StraponPart original) {
        super(original);
    }

    @Override
    public StraponPart copy() {
        return new StraponPart(this);
    }
}
