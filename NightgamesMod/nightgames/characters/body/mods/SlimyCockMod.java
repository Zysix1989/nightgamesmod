package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Horny;

public class SlimyCockMod extends CockMod {

    public SlimyCockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);

        return bonus;
    }

    public Optional<String> getFluids() {
        return Optional.empty();
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        return 0;
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, boolean selfCame) {
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan,
        BodyPart part) {

    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target) {

    }

    @Override
    public void onEndPenetration(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target) {
        c.write(self, Global.format(
            "As {self:possessive} {self:body-part:cock} leaves {other:possessive} "
                + target.describe(opponent)
                + ", a small bit of slime stays behind, vibrating inside of {other:direct-object}.",
            self, opponent));
        opponent.add(c, new Horny(opponent, Math.max(4, opponent.getArousal().max() / 20), 10,
            self.nameOrPossessivePronoun() + " slimy residue"));
    }

    public static Optional<CockMod> getFromType(String type) {
        return Optional.of(CockMod.slimy);
    }

    @Override
    public String describeAdjective(String partType) {
        return "slimy transparency";
    }
}
