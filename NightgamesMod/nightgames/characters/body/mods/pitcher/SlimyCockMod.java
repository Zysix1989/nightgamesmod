package nightgames.characters.body.mods.pitcher;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.mods.catcher.CatcherMod;
import nightgames.characters.body.mods.catcher.GooeyMod;
import nightgames.combat.Combat;

public class SlimyCockMod extends CockMod {
    public static final String TYPE = "slimy";
    public SlimyCockMod() {
        super(TYPE, .5, 1.5, .7);
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

    @Override
    public CatcherMod getCorrespondingCatcherMod() {
        return new GooeyMod();
    }
}
