package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;

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

    @Override
    public String describeAdjective(String partType) {
        return "slimy transparency";
    }
}
