package nightgames.characters.body;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class FeetPart extends GenericBodyPart {

    public static final String TYPE = "feet";

    public FeetPart() {
        super("feet", 0, 1, 1, TYPE, "");
    }

    @Override
    public double getPleasure(Character self) {
        var mod = super.getPleasure(self);
        mod += self.has(Trait.limbTraining1) ? .5 : 0;
        mod += self.has(Trait.limbTraining2) ? .7 : 0;
        mod += self.has(Trait.limbTraining3) ? .7 : 0;
        mod += self.has(Trait.dexterous) ? .4 : 0;
        return mod;
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage,
        Combat c) {
        var bonus = super.applyBonuses(self, opponent, target, damage, c);
        if (self.has(Trait.nimbletoes)) {
            c.write(self, Global.format("{self:name-possessive} nimble toes adeptly "
                + "massage {other:possessive} " + target.describe(opponent)
                + " elicting a quiet gasp.", self, opponent));
            bonus += Global.random(2, 6);
        }
        return bonus;
    }

    @Override
    public boolean isMultipleObjects() {
        return true;
    }
}
