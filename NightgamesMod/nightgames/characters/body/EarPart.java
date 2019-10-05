package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;

public abstract class EarPart implements BodyPart {
    public String desc;
    public double hotness;
    public double pleasure;
    public double sensitivity;

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

    @Override
    public String canonicalDescription() {
        return desc + "ears";
    }

    @Override
    public String fullDescribe(Character c) {
        return desc + "ears";
    }

    @Override
    public double priority(Character c) {
        return getPleasure(c, null);
    }

    @Override
    public double getHotness(Character self, Character opponent) {
        return hotness;
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        return pleasure;
    }

    @Override
    public double getSensitivity(Character self, BodyPart target) {
        return sensitivity;
    }

    @Override
    public double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage,
        Combat c) {
        return 0;
    }

    @Override
    public int counterValue(BodyPart otherPart, Character self, Character other) {
        return 0;
    }

    @Override
    public boolean isVisible(Character c) {
        return true;
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append(
            Global.capitalizeFirstLetter(fullDescribe(c)) + " frames " + c.possessiveAdjective()
                + " face.");
    }

    @Override
    public String toString() {
        return desc + "ears";
    }

    @Override
    public boolean isType(String type) {
        return type.equalsIgnoreCase("ears");
    }

    @Override
    public String getType() {
        return "ears";
    }

    @Override
    public boolean isReady(Character self) {
        return true;
    }

    @Override
    public String getFluids(Character c) {
        return "";
    }

    @Override
    public boolean isErogenous() {
        return false;
    }

    @Override
    public BodyPart upgrade() {
        return this;
    }

    @Override
    public BodyPart downgrade() {
        return this;
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) { }

    @Override
    public int compare(BodyPart other) {
        return 0;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        return 0;
    }

    @Override
    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        return 0;
    }

    @Override
    public String prefix() {
        return "";
    }

    @Override
    public String adjective() {
        return "otic";
    }
}
