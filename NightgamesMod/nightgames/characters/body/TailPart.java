package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;

public class TailPart extends GenericBodyPart {

    public static final String TYPE = "tail";
    public TailPart() {
        super("tail", 0, 0, 0, true, TYPE, "a ");
    }

    public TailPart(JsonObject js) {
        super(js);
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("A lithe " + describe(c) + " swings lazily behind " + c.nameOrPossessivePronoun() + " back.");
    }

    @Override
    public boolean getDefaultErogenous() {
        return false;
    }

    @Override
    public String adjective() {
        return "tail";
    }
}
