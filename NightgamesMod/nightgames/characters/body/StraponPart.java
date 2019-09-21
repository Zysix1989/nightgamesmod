package nightgames.characters.body;

import com.google.gson.JsonObject;

public class StraponPart extends GenericBodyPart {

    public StraponPart() {
        super("strap-on", 1, 1, -999, "strapon", "a ");
    }

    public StraponPart(JsonObject js) {
        super(js);
    }
}
