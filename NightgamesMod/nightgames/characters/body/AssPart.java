package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Optional;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.mods.PartMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.Drained;
import nightgames.status.Stsflag;
import nightgames.status.Trance;

public class AssPart extends GenericBodyPart {
    public static String TYPE = "ass";

    public enum Size {
        Small(0,"small"),
        Normal(1, ""),
        Girlish(2, "girlish"),
        Flared(3, "flared"),
        Large(4, "large"),
        Huge(5, "huge");

        private static HashMap<Integer, Size> map = new HashMap<>();

        static {
            for (Size s : Size.values()) {
                map.put(s.value, s);
            }
        }

        private static Optional<Size> fromValue(int v) {
            return Optional.of(map.get(v));
        }
        public static Size max() {
            return Huge;
        }
        public static Size min() {
            return Small;
        }
        private int value;
        private String description;

        Size(int v, String description) {
            value = v;
            this.description = description;
        }
    }

    private Size size;

    private AssPart() {
        super("ass", "", 0, 1.2, 1, false, AssPart.TYPE, "a ");
    }

    public AssPart(JsonObject js) {
        super(js);
        size = Size.fromValue(js.get("size").getAsInt()).orElseThrow();
    }

    public AssPart(Size size) {
        this();
        this.size = size;
    }

    @Override
    public double getFemininity(Character c) {
        return getSize().value - Size.Girlish.value;
    }

    @Override
    public double getHotness(Character self, Character opponent) {
        double hotness = super.getHotness(self, opponent);

        Clothing top = self.getOutfit().getTopOfSlot(ClothingSlot.bottom);
        hotness += -.1 + Math.sqrt(getSize().value) * .2 * self.getOutfit()
                                                .getExposure(ClothingSlot.bottom);
        if (!opponent.hasDick()) {
            hotness /= 2;
        }
        if (top == null) {
            hotness += .1;
        }
        return Math.max(0, hotness);
    }

    @Override
    public int mod(Attribute a, int total) { 
        int bonus = super.mod(a, total);
        if (getSize().value > Size.Normal.value & a == Attribute.Seduction) {
            bonus += (getSize().value - Size.Normal.value) * 2;
        }
        if (getSize().value > Size.Flared.value & a == Attribute.Speed) {
            bonus += (getSize().value - Size.Flared.value);
        }
        return bonus;
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        double pleasureMod = super.getPleasure(self, target);
        pleasureMod += self.has(Trait.analTraining1) ? .5 : 0;
        pleasureMod += self.has(Trait.analTraining2) ? .7 : 0;
        pleasureMod += self.has(Trait.analTraining3) ? .7 : 0;
        return pleasureMod;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = super.applyBonuses(self, opponent, target, damage, c);
        if (self.has(Trait.oiledass) && c.getStance().anallyPenetratedBy(c, self, opponent)) {
            c.write(self, Global.format(
                            "{self:NAME-POSSESSIVE} naturally oiled asshole swallows {other:name-possessive} cock with ease.",
                            self, opponent));
            bonus += 5;
        }

        if (self.canRespond() && (self.has(Trait.tight) || self.has(Trait.holecontrol)) && c.getStance().anallyPenetrated(c, self)) {
            String desc = "";
            if (self.has(Trait.tight)) {
                desc += "powerful ";
            }
            if (self.has(Trait.holecontrol)) {
                desc += "well-trained ";
            }
            c.write(self, Global.format(
                            "{self:SUBJECT-ACTION:use|uses} {self:possessive} " + desc
                                            + "sphincter muscles to milk {other:name-possessive} cock, adding to the pleasure.",
                            self, opponent));
            bonus += self.has(Trait.tight) && self.has(Trait.holecontrol) ? 10 : 5;
            if (self.has(Trait.tight)) {
                opponent.pain(c, self, Math.min(30, self.get(Attribute.Power)));
            }
            if (!c.getStance().mobile(opponent) || !opponent.canRespond()) {
                bonus /= 5;
            }
        }
        if (self.has(Trait.drainingass) && !target.isType(StraponPart.TYPE)) {
            if (Global.random(3) == 0) {
                c.write(self, Global.format("{self:name-possessive} ass seems to <i>inhale</i>, drawing"
                                + " great gouts of {other:name-possessive} strength from {other:possessive}"
                                + " body.", self, opponent));
                opponent.drain(c, self, self.getLevel());
                Drained.drain(c, self, opponent, Attribute.Power, 3, 10, true);
            } else {
                c.write(self, Global.format("The feel of {self:name-possessive} ass around"
                                + " {other:name-possessive} %s drains"
                                + " {other:direct-object} of {other:possessive} energy.", self, opponent, target.describe(opponent)));
                opponent.drain(c, self, self.getLevel()/2);
            }
        }
        return bonus;
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {
        super.tickHolding(c, self, opponent, otherOrgan);
        if (self.has(Trait.autonomousAss)) {
            c.write(self, Global.format(
                            "{self:NAME-POSSESSIVE} " + fullDescribe(self)
                                            + " churns against {other:name-possessive} cock, "
                                            + "seemingly with a mind of its own. {self:POSSESSIVE} internal muscles feel like a hot fleshy hand inside her asshole, jerking {other:possessive} shaft.",
                            self, opponent));
            opponent.body.pleasure(self, this, otherOrgan, 10, c);
        }
    }

    @Override
    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = super.applyReceiveBonuses(self, opponent, target, damage, c);
        if ((opponent.has(Trait.asshandler)
                || opponent.has(Trait.anatomyknowledge))
            && opponent.canRespond()
            && c.getStance().mobile(opponent)) {
            c.write(opponent,
                            Global.format("{other:NAME-POSSESSIVE} expert handling of {self:name-possessive} ass causes {self:subject} to shudder uncontrollably.",
                                            self, opponent));
            bonus += 5;
        }
        if (self.has(Trait.buttslut)) {
            bonus += 10;
            if (Global.random(4) == 0 && !self.is(Stsflag.trance)) {
                c.write(opponent, Global.format(
                                "The foreign object rummaging around inside {self:name-possessive} ass <i><b>just feels so right</b></i>. {self:SUBJECT-ACTION:feel|feels} {self:reflective} slipping into a trance!",
                                                self, opponent));
                self.add(c, new Trance(self, 3, false));
            }
            c.write(opponent, Global.format(
                            "The foreign object rummaging around inside {self:name-possessive} ass feels so <i>right</i>. {self:SUBJECT} can't help moaning in time with the swelling pleasure.",
                                            self, opponent));

        }
        return bonus;
    }

    @Override
    public boolean isReady(Character c) {
        return c.has(Trait.oiledass) || c.is(Stsflag.oiled);
    }

    public String getFluidsNoMods(Character c) {
        if (c.has(Trait.oiledass)) {
            return "oils";
        }
        return "";
    }

    @Override
    public boolean getDefaultErogenous() {
        return true;
    }

    @Override
    public double priority(Character c) {
            return (c.has(Trait.tight) ? 1 : 0) + (c.has(Trait.holecontrol) ? 1 : 0) + (c.has(Trait.oiledass) ? 1 : 0)
                            + (c.has(Trait.autonomousAss) ? 4 : 0);
    }

    @Override
    public String adjective() {
        return "anal";
    }

    public BodyPart upgrade() {
        return new AssPart(Size.fromValue(Global.clamp(
            getSize().value + 1,
            Size.min().value,
            Size.max().value)).orElseThrow());
    }

    public BodyPart downgrade() {
        return new AssPart(Size.fromValue(Global.clamp(
            getSize().value - 1,
            Size.min().value,
            Size.max().value)).orElseThrow());
    }
    @Override
    public double getSensitivity(Character self, BodyPart target) {
        double sensitivityMod = sensitivity;
        double bonus = 1.0;
        for (PartMod mod : mods) {
            bonus += mod.getSensitivity();
        }
        return sensitivityMod * bonus;
    }

    public Size getSize() {
        return size;
    }
}
