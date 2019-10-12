package nightgames.characters.body;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;

public class HandsPart extends GenericBodyPart {
    public static final String TYPE = "hands";

    public HandsPart() {
        super("hands", 0, 1, 1, TYPE, "");
    }

    @Override
    public double getPleasure(Character self) {
        var mod = super.getPleasure(self);
        mod += self.has(Trait.limbTraining1) ? .5 : 0;
        mod += self.has(Trait.limbTraining2) ? .7 : 0;
        mod += self.has(Trait.limbTraining3) ? .7 : 0;
        mod += self.has(Trait.dexterous) ? .4 : 0;
        mod += self.has(Trait.pimphand) ? .2 : 0;
        return mod;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage,
        Combat c) {
        var bonus = super.applyBonuses(self, opponent, target, damage, c);
        if (self.has(ClothingTrait.nursegloves)) {
            c.write(self, Global.format("{self:name-possessive} rubber gloves provide a "
                + "unique sensation as {self:subject-action:run|runs} {self:possessive} "
                + "hands over {other:possessive} " + target.describe(opponent) + ".", self, opponent));
            bonus += 5 + Global.random(5);
            if (Global.random(5) == 0) {
                c.write(self, "Unfortunately, the gloves wear out with their usage.");
                self.shred(ClothingSlot.arms);
            }
        }
        if (self.has(Trait.defthands)) {
            c.write(self, Global.format("{self:name-possessive} hands dance "
                + "across {other:possessive} " + target.describe(opponent) +
                ", hitting all the right spots.", self, opponent));
            bonus += Global.random(2, 6);
        }
        return bonus;
    }
}
