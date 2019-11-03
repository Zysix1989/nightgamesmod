package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class TentaclePart extends GenericBodyPart {

    public static final String TYPE = "tentacles";
    String attachpoint;
    private String fluids;

    public static void pleasureWithTentacles(Combat c, Character target, int strength, BodyPart targetPart) {
        target.body.pleasure(c.getOpponent(target), new TentaclePart(), targetPart, strength, c);
    }

    public TentaclePart(String desc, String attachpoint, String fluids, double hotness, double pleasure,
                    double sensitivity) {
        super(desc, "", hotness, pleasure, sensitivity, true, TYPE, "");
        this.attachpoint = attachpoint;
        this.fluids = fluids;
    }

    public TentaclePart() {
        super("tentacles", 1.0, 1.0, 0.0, TentaclePart.TYPE, "");
    }

    public TentaclePart(JsonObject js) {
        super(js);
    }

    protected TentaclePart(TentaclePart original) {
        super(original);
        attachpoint = original.attachpoint;
        fluids = original.fluids;
    }

    public static String synonyms[] = {"mass", "clump", "nest", "group",};

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("A " + Global.pickRandom(synonyms).get() + " of ");
        b.append(describe(c));
        if (c.body.has(attachpoint)) {
            b.append(" sprouts from " + c.nameOrPossessivePronoun() + " " + attachpoint + ".");
        } else {
            b.append(" sprouts from " + c.nameOrPossessivePronoun() + " back.");
        }
    }

    @Override
    public String describe(Character c) {
        return desc;
    }

    @Override
    public String fullDescribe(Character c) {
        return attachpoint + " " + desc;
    }

    @Override
    public boolean isReady(Character c) {
        return true;
    }

    @Override
    public String getFluids(Character c) {
        return fluids;
    }

    @Override
    public TentaclePart copy() {
        return new TentaclePart(this);
    }
}
