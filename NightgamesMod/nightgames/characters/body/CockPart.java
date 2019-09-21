package nightgames.characters.body;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.mods.IncubusCockMod;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.SizeMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.Sensitized;

public class CockPart extends GenericBodyPart {
    public enum Size implements Comparable<Size> {
        Tiny(3),
        Small(4),
        Little(5),
        Average(6),
        Large(7),
        Big(8),
        Huge(9),
        Massive(10),
        Colossal(11),
        Mammoth(12);

        private static HashMap<Integer, Size> map = new HashMap<>();

        static {
            for (Size s : Size.values()) {
                map.put(s.value, s);
            }
        }

        private static Optional<Size> fromValue(int v) {
            return Optional.of(map.get(v));
        }

        private int value;

        Size(int v) {
            value = v;
        }
    }

    //FIXME: Sort out how Breast got support for mutation completely separate from cocks.
    
    public static String synonyms[] = {"cock", "dick", "shaft", "phallus"};

    private static final Map<Size, String> COCK_SIZE_DESCRIPTIONS = new HashMap<>();
    static {
        COCK_SIZE_DESCRIPTIONS.put(Size.Tiny, "tiny ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Small, "small ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Little, "small ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Average, "");
        COCK_SIZE_DESCRIPTIONS.put(Size.Large, "big ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Big, "huge ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Huge, "gigantic ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Massive, "massive ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Colossal, "colossal ");
        COCK_SIZE_DESCRIPTIONS.put(Size.Mammoth, "mammoth ");
    }

    private Size size;

    public CockPart() {
        super("cock", "", 0, 1.2, 1, false, "cock", "a ");
    }

    public CockPart(JsonObject js) {
        super(js);
        size = Size.fromValue(js.get("size").getAsInt()).orElseThrow();
    }

    public CockPart(int size) {
        this();
        this.size = Size.fromValue(size).orElseThrow();
    }

    @Override
    public double getFemininity(Character c) {
        return Size.Small.value - getSize();
    }

    @Override
    public int mod(Attribute a, int total) { 
        int bonus = super.mod(a, total);
        int size = getSize();
        if (size > Size.Average.value & a == Attribute.Seduction) {
            bonus += (size - Size.Average.value) * 2;
        }
        if (size > Size.Big.value & a == Attribute.Speed) {
            bonus += (size - Size.Big.value);
        }
        return bonus;
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        double pleasureMod = super.getPleasure(self, target);
        pleasureMod += self.has(Trait.sexTraining1) ? .5 : 0;
        pleasureMod += self.has(Trait.sexTraining2) ? .7 : 0;
        pleasureMod += self.has(Trait.sexTraining3) ? .7 : 0;
        return pleasureMod;
    }

    @Override
    protected String modlessDescription(Character c) {
        String syn = Global.pickRandom(synonyms).get();
        return (c != null && c.hasPussy() && !moddedPartCountsAs(IncubusCockMod.TYPE) ? "girl-" : "") + syn;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = super.applyBonuses(self, opponent, target, damage, c);
        if (self.has(Trait.polecontrol) && self.canRespond()) {
            String desc = "";
            if (self.has(Trait.polecontrol)) {
                desc += "expert ";
            }
            c.write(self, Global.format(
                            "{self:SUBJECT-ACTION:use|uses} {self:possessive} " + desc
                                            + "cock control to grind against {other:name-possessive} inner walls, making {other:possessive} knuckles whiten as {other:pronoun} {other:action:moan|moans} uncontrollably.",
                            self, opponent));
            bonus += self.has(Trait.polecontrol) ? 8 : 0;
        }
        return bonus;
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {
        super.tickHolding(c, self, opponent, otherOrgan);
    }

    @Override
    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = super.applyReceiveBonuses(self, opponent, target, damage, c);
        if ((opponent.has(Trait.dickhandler) || opponent.has(Trait.anatomyknowledge)) && opponent.canRespond() && c.getStance().mobile(opponent)) {
            c.write(opponent,
                            Global.format("{other:NAME-POSSESSIVE} expert handling of {self:name-possessive} cock causes {self:subject} to shudder uncontrollably.",
                                            self, opponent));
            if (opponent.has(Trait.dickhandler)) {
                bonus += 5;
            }
            if (opponent.has(Trait.anatomyknowledge)) {
                bonus += 5;
            }
        }
        if (self.has(Trait.druglacedprecum) && !opponent.isPartProtected(target)) {
            opponent.add(c, new Sensitized(opponent, target, .2, 2.0, 20));
            c.write(self, Global.format("{self:NAME-POSSESSIVE} drug-laced precum is affecting {other:direct-object}.",
                            self, opponent));
        }
        return bonus;
    }

    @Override
    public boolean isReady(Character c) {
        return c.getArousal().percent() >= 15 || c.has(Trait.alwaysready);
    }

    public String getFluidsNoMods(Character c) {
        return "semen";
    }

    @Override
    public boolean getDefaultErogenous() {
        return true;
    }

    @Override
    public double priority(Character c) {
        return 2 + (c.has(Trait.polecontrol)? 2 : 0) + (c.has(Trait.druglacedprecum) ? 1 : 0)+ (c.has(Trait.hypnoticsemen) ? 2 : 0);
    }

    @Override
    public String adjective() {
        return "phallic";
    }

    @Override
    public boolean isVisible(Character c) {
        return c.crotchAvailable() || getSize() > Size.Average.value;
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        b.append("There is a ");
        if (c.crotchAvailable()) {
            b.append(fullDescribe(c));
            if (c.getArousal().percent() <= 15) {
                b.append(" hanging between ");
            } else if (c.getArousal().percent() <= 50 ) {
                b.append(" erect between ");
            } else {
                b.append(" proudly erect between ");
            }
            b.append(c.nameOrPossessivePronoun());
            b.append(" legs.");
        } else {
            b.append(sizeAdjective());
            b.append(" bulge in ");
            b.append(c.possessiveAdjective());
            b.append(" ");
            b.append(Optional.ofNullable(c.getOutfit().getTopOfSlot(ClothingSlot.bottom)).map(Clothing::getName).orElse("outfit"));
            b.append(" where ");
            b.append(c.possessiveAdjective());
            b.append(" crotch is.");
        }
    }

    private String sizeAdjective() {
        return COCK_SIZE_DESCRIPTIONS.get(size);
    }

    public BodyPart upgrade() {
        return new CockPart(SizeMod.clampToValidSize(this, getSize() + 1));
    }

    public BodyPart downgrade() {
        return new CockPart(SizeMod.clampToValidSize(this, getSize() - 1));
    }

    public PussyPart getEquivalentPussy() {
        List<PartMod> newMods = getPartMods().stream().map(BodyUtils.EQUIVALENT_MODS::get).filter(mod -> mod != null).distinct().collect(Collectors.toList());
        GenericBodyPart newPart = PussyPart.generateGeneric();
        for (PartMod mod : newMods) {
            newPart = (GenericBodyPart)newPart.applyMod(mod);
        }
        return (PussyPart)newPart;
    }

    public int getSize() {
        return size.value;
    }
}
