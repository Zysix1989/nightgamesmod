package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class TentaclePart extends GenericBodyPart {

    public static final String TYPE = "tentacles";
    public String attachpoint;
    private String fluids;
    private static String[] allowedAttachTypes = {AssPart.TYPE, MouthPart.TYPE, PussyPart.TYPE, Body.HANDS, Body.FEET, TailPart.TYPE, CockPart.TYPE};

    public static void pleasureWithTentacles(Combat c, Character target, int strength, BodyPart targetPart) {
        target.body.pleasure(c.getOpponent(target), new TentaclePart(), targetPart, strength, c);
    }

    public static TentaclePart randomTentacle(String desc, Body body, String fluids) {
        Set<String> avail = new HashSet<>(Arrays.asList(allowedAttachTypes));
        Set<String> parts = new HashSet<>();
        for (BodyPart p : body.getCurrentParts()) {
            if (p instanceof TentaclePart) {
                avail.remove(((TentaclePart) p).attachpoint);
            }
            parts.add(p.getType());
        }

        avail.retainAll(parts);
        String type;
        ArrayList<String> availList = new ArrayList<String>(avail);
        if (avail.size() > 0) {
            type = availList.get(Global.random(availList.size()));
        } else {
            type = "back";
        }
        return new TentaclePart(desc, type, fluids, 0, 1, 1);
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
    public double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage,
                    Combat c) {
        if (with.isType(attachpoint) && Global.random(3) > -1) {
            c.write(self, Global.format("Additionally, {self:name-possessive} " + fullDescribe(self)
                            + " take the opportunity to squirm against {other:name-possessive} "
                            + target.fullDescribe(opponent) + ".", self, opponent));
            opponent.body.pleasure(self, this, target, 5, c);
        }
        return 0;
    }

    @Override
    public boolean isReady(Character c) {
        return true;
    }

    @Override
    public String getFluids(Character c) {
        return fluids;
    }
}
