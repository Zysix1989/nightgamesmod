package nightgames.status;

import java.util.Optional;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

/**
 * A special stun
 */
public class Plasticized extends DurationStatus {
    public Plasticized(Character affected) {
        this(affected, 4);
    }

    public Plasticized(Character affected, int duration) {
        super("Plasticized", affected, duration);
        flag(Stsflag.stunned);
        flag(Stsflag.plasticized);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        flag(Stsflag.disabling);
    }

    @Override
    public String describe(Character opponent) {
        if (affected.human()) {
            return "You completely immobilized in a skin of hard plastic.";
        } else {
            return affected.getName() + " is completedly immobilized in a suit of hard plastic.";
        }
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        if (affected.human()) {
            return "<b>You are wrapped in a layer of hard plastic and are completely immobilized!</b>";
        } else {
            return "<b>" + affected.getName() + " is completedly immobilized in a coating of hard plastic!</b>";
        }
    }

    @Override
    public float fitnessModifier() {
        return -40f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "{{ affected.subjectAction('are', 'is') }} finally freed of " +
                "{{ affected.possessiveAdjective() }} plastic prison!");
        JtwigModel model = JtwigModel.newModel()
            .with("affected", affected);
        Global.writeIfCombat(c, affected, template.render(model));
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        if (c != null && c.getStance().mobile(affected)) {
        	c.write(affected, Global.format("It's impossible for {self:name-do} to stay on {self:possessive} feet.", affected, c.getOpponentCharacter(affected)));
        	affected.add(c, new Falling(affected));
        }
        affected.emote(Emotion.nervous, 5);
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return -x;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return -200;
    }

    @Override
    public int escape() {
        return -200;
    }

    @Override
    public int gainmojo(int x) {
        return -x;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return -200;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Plasticized(newAffected, getDuration());
    }

    @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Plasticized(Global.noneCharacter(), obj.get("duration").getAsInt());
    }
}
