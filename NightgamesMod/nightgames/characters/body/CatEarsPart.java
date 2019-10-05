package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;

public class CatEarsPart extends EarPart implements BodyPartMod {
    public static final String TYPE = "cat";

    public CatEarsPart() {
        this.desc = "cat ";
        this.hotness = .15;
        this.pleasure = 1.5;
        this.sensitivity = 1.5;
    }

    @Override
    public String canonicalDescription() {
        return desc + "ears";
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("Cute " + fullDescribe(c) + " tops " + c.possessiveAdjective() + " head.");
    }

    @Override
    public String describe(Character c) {
        return desc + "ears";
    }

    @Override
    public double priority(Character c) {
        return getPleasure(c, null);
    }

    @Override
    public String fullDescribe(Character c) {
        return desc + "ears";
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
    public boolean isReady(Character self) {
        return true;
    }

    @Override public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enum", TYPE);
        return obj;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        return 0;
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
    public boolean isNotable() {
        return true;
    }

    @Override
    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        return 0;
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
    public String prefix() {
        return "";
    }

    @Override
    public int compare(BodyPart other) {
        return 0;
    }

    @Override
    public boolean isVisible(Character c) {
        return true;
    }

    @Override
    public double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage,
        Combat c) {
        return 0;
    }

    @Override
    public int mod(Attribute a, int total) {
        if (a == Attribute.Seduction) {
            return 3;
        }
        return 0;
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {

    }

    @Override
    public int counterValue(BodyPart otherPart, Character self, Character other) {
        return 0;
    }

    @Override
    public Collection<BodyPartMod> getMods() {
        return Collections.singleton(this);
    }

    @Override
    public String getModType() {
        return TYPE;
    }

    @Override
    public String adjective() {
        return "otic";
    }
}
